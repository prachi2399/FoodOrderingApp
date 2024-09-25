package com.FoodApp.FoodOrderingApp.service;

import com.FoodApp.FoodOrderingApp.constants.OrderStatus;
import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.dto.MenuQuantity;
import com.FoodApp.FoodOrderingApp.dto.OrderDetails;
import com.FoodApp.FoodOrderingApp.entities.MenuItem;
import com.FoodApp.FoodOrderingApp.entities.Order;
import com.FoodApp.FoodOrderingApp.entities.Restaurant;
import com.FoodApp.FoodOrderingApp.repository.MenuItemsRepository;
import com.FoodApp.FoodOrderingApp.repository.OrderRepository;
import com.FoodApp.FoodOrderingApp.repository.RestaurantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.FoodApp.FoodOrderingApp.mappers.Mapper.menuOrderMapper;

@Log4j2
@Component
public class OrderProcessingService {
    @Autowired
    private RestaurantSelectionStrategyService restaurantStrategy;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MenuItemsRepository menuItemsRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    private final ConcurrentHashMap<Long, ReentrantLock> restaurantLocks = new ConcurrentHashMap<>();

    @KafkaListener(topics = "orders", groupId = "order-group")
    public void consumeOrder(String orderJson) {
        try {
            log.info("consumeOrder {}", orderJson);
            // Deserialize the order JSON back to OrderDetails
            OrderDetails orderDetails = objectMapper.readValue(orderJson, OrderDetails.class);

            // Process the order as before
            processOrder(orderDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
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

    @Transactional
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
            restaurant = restaurantRepository.findById(restaurant.getId()).orElse(null);
            if (canProcessOrder(restaurant, orderDetails)) {
//                restaurantRepository.save(restaurant);
                restaurantService.updateRestaurantCapacity(restaurant.getId(), restaurant.getCurrentCapacity()+1);
                List<String> menuItem = orderDetails.getMenuItemList().stream().map(MenuQuantity::getMenuItem).toList();
                List<MenuItem> menuItemList = menuItemsRepository.findByMenuIdAndNameIn(restaurant.getMenu().getId(), menuItem);
                restaurant = restaurantRepository.findById(restaurant.getId()).orElse(null);
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


}
