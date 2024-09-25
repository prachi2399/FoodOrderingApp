package com.FoodApp.FoodOrderingApp.service;

import com.FoodApp.FoodOrderingApp.constants.OrderStatus;
import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.dto.MenuDTO;
import com.FoodApp.FoodOrderingApp.dto.MenuQuantity;
import com.FoodApp.FoodOrderingApp.dto.OrderDetails;
import com.FoodApp.FoodOrderingApp.entities.MenuItem;
import com.FoodApp.FoodOrderingApp.entities.Order;
import com.FoodApp.FoodOrderingApp.entities.Restaurant;
import com.FoodApp.FoodOrderingApp.repository.MenuItemsRepository;
import com.FoodApp.FoodOrderingApp.repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.FoodApp.FoodOrderingApp.mappers.Mapper.menuOrderMapper;
@Component
@Log4j2
public class OrderServiceImpl implements OrderService{
    @Autowired
    private RestaurantSelectionStrategyService restaurantStrategy;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MenuItemsRepository menuItemsRepository;

    private final BlockingQueue<OrderDetails> orderQueue = new LinkedBlockingQueue<>();
    private final ConcurrentHashMap<Long, ReentrantLock> restaurantLocks = new ConcurrentHashMap<>();
    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this::processOrders);
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdownNow();
    }

    public void placeOrder(OrderDetails order){
        log.info("placing order {}", orderQueue.size());
        orderQueue.offer(order);
//        processOrder(order);
    }



    public void processOrders(){
        while(true){
            try{
                log.info("processing messages {}", orderQueue.size());
                OrderDetails order = orderQueue.take();
                processOrder(order);
            }catch (InterruptedException e){
                log.error("Error thread interuppted order: " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            } catch (CustomException e) {
                log.error("Error processing order: " + e.getMessage());
            }
        }
    }
    public void processOrder(OrderDetails order) throws CustomException {
        log.info("process Order {}", order.getCustomerId());
        List<String> menuList = order.getMenuItemList().stream().map(MenuQuantity::getMenuItem).collect(Collectors.toCollection(ArrayList::new));
        Restaurant selectedRestaurant = restaurantStrategy.selectRestaurant(order.getCity(), menuList, order.getRestaurantStrategyName());
        log.info("selectedRestaurant {}", selectedRestaurant);
        if (Objects.nonNull(selectedRestaurant) && canProcessOrder(selectedRestaurant, order)) {
            reserveCapacity(selectedRestaurant,order);
        }
    }

    private boolean canProcessOrder(Restaurant restaurant, OrderDetails order) {
        log.info("canProcessOrder");
        return restaurant.getCurrentCapacity() + 1 <= restaurant.getProcessingCapacity();
    }

    public void reserveCapacity(Restaurant restaurant,OrderDetails orderDetails) throws CustomException {
        log.info("reserving capacitu for restaurant {}", restaurant.getId());
        ReentrantLock lock = restaurantLocks.computeIfAbsent(restaurant.getId(), k-> new ReentrantLock());
        boolean isLockAcquired = false;
        lock.lock();
        try {
            isLockAcquired = lock.tryLock(5, TimeUnit.SECONDS);
            if (!isLockAcquired) {
                throw new CustomException("Could not acquire lock for restaurant " + restaurant.getName() + " within timeout period");
            }
            if (canProcessOrder(restaurant, orderDetails)) {
            restaurantService.updateRestaurantCapacity(restaurant.getId(), restaurant.getCurrentCapacity()+1);
            List<String> menuItem = orderDetails.getMenuItemList().stream().map(MenuQuantity::getMenuItem).toList();
            List<MenuItem> menuItemList = menuItemsRepository.findByMenuIdAndNameIn(restaurant.getMenu().getId(), menuItem);
            orderRepository.save(Order.builder().
                    status(OrderStatus.ACCEPTED).
                    city(orderDetails.getCity()).
                    restaurantSelected(restaurant).
                    items(menuOrderMapper(orderDetails.getMenuItemList(), menuItemList)).
                    customerId(orderDetails.getCustomerId()).
                    build());
            } else {
                throw new CustomException("Insufficient capacity at " + restaurant.getName());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (isLockAcquired) {
                lock.unlock();
            }
        }
    }

    public void completeOrder(Restaurant restaurant, Order order) {
        ReentrantLock lock = restaurantLocks.get(restaurant.getId());
        if (lock != null) {
            lock.lock();
            try {
                restaurantService.updateRestaurantCapacity(restaurant.getId(), restaurant.getCurrentCapacity() - 1);
                order.setStatus(OrderStatus.DISPACTED);
                orderRepository.save(order);
            } catch (CustomException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public String takeOrder(OrderDetails orderDetails) throws CustomException {
        List<String> menuList = orderDetails.getMenuItemList().stream().map(MenuQuantity::getMenuItem).collect(Collectors.toCollection(ArrayList::new));
        Restaurant selectedRestaurant = restaurantStrategy.selectRestaurant(orderDetails.getCity(), menuList, orderDetails.getRestaurantStrategyName());
        if(Objects.isNull(selectedRestaurant)){
            throw new CustomException("No nearby accepting delivery for this order");
        }
        try {
            placeOrder(orderDetails);
            return OrderStatus.ACCEPTED.toString();
        } catch (Exception e) {
            throw new CustomException("An unexpected error occurred while placing the order", e);
        }
    }

    @Override
    public void completeOrder(Long orderId) throws CustomException {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new CustomException("No resturant exist with id "+ orderId));
        completeOrder(order.getRestaurantSelected(), order);
    }

}
