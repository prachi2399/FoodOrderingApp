package com.FoodApp.FoodOrderingApp.service;

import com.FoodApp.FoodOrderingApp.constants.OrderStatus;
import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.dto.MenuQuantity;
import com.FoodApp.FoodOrderingApp.dto.OrderDetails;
import com.FoodApp.FoodOrderingApp.dto.StandardResponse;
import com.FoodApp.FoodOrderingApp.entities.Menu;
import com.FoodApp.FoodOrderingApp.entities.MenuItem;
import com.FoodApp.FoodOrderingApp.entities.Order;
import com.FoodApp.FoodOrderingApp.entities.Restaurant;
import com.FoodApp.FoodOrderingApp.repository.MenuItemsRepository;
import com.FoodApp.FoodOrderingApp.repository.OrderRepository;
import com.FoodApp.FoodOrderingApp.repository.RestaurantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
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
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemsRepository menuItemsRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final ConcurrentHashMap<Long, ReentrantLock> restaurantLocks = new ConcurrentHashMap<>();

    private static final String TOPIC = "orders";

    @Override
    public StandardResponse<String> takeOrder(OrderDetails orderDetails) throws CustomException {
        List<String> menuList = orderDetails.getMenuItemList().stream().map(MenuQuantity::getMenuItem).collect(Collectors.toCollection(ArrayList::new));
        StandardResponse standardResponse = new StandardResponse<>();
        if(isOrderNotServiceable(orderDetails.getCity(), menuList)){
            standardResponse.setSuccess(false);
            standardResponse.setError("The menu items are not serviceable by any restaurant");
            standardResponse.setMessage("The menu items are not serviceable by any restaurant");
            return standardResponse;
        }
        Restaurant selectedRestaurant = restaurantStrategy.selectRestaurant(orderDetails.getCity(), menuList, orderDetails.getRestaurantStrategyName());

        if(Objects.isNull(selectedRestaurant)){
            standardResponse.setError("No nearby Restaurant accepting delivery for this order");
            standardResponse.setSuccess(true);
            return standardResponse;
        }

        ReentrantLock lock = restaurantLocks.computeIfAbsent(selectedRestaurant.getId(), k-> new ReentrantLock());
        boolean isLockAcquired = false;
        lock.lock(); // Lock the critical section
        try {
            isLockAcquired = lock.tryLock(5, TimeUnit.SECONDS);
            if (!isLockAcquired) {
                throw new CustomException("Could not acquire lock for restaurant " + selectedRestaurant.getName() + " within timeout period");
            }

            // Check if restaurant has enough capacity
            if (selectedRestaurant.getCurrentCapacity() >= selectedRestaurant.getProcessingCapacity()) {
                standardResponse.setSuccess(false);
                standardResponse.setError("The selected restaurant is at full capacity");
                return standardResponse;
            }

            // Reserve capacity
            selectedRestaurant.setCurrentCapacity(selectedRestaurant.getCurrentCapacity() + 1);
            restaurantRepository.save(selectedRestaurant); // Save updated capacity

            // Save the order
            Order order = saveOrder(orderDetails, selectedRestaurant);
            orderDetails.setOrderId(order.getID());

            // Publish to Kafka
            String orderJson = objectMapper.writeValueAsString(order);
            kafkaTemplate.send(TOPIC, orderJson);

            log.info("Order sent to Kafka");
            standardResponse.setData(OrderStatus.ACCEPTED.toString());
            standardResponse.setSuccess(true);
        } catch (Exception e) {
            standardResponse.setError(e.getMessage());
            standardResponse.setSuccess(false);
            log.error("Error while processing order", e);
        } finally {
            lock.unlock(); // Always unlock in a finally block to avoid deadlocks
        }
        return standardResponse;
    }

    public Order saveOrder(OrderDetails orderDetails, Restaurant restaurant) throws CustomException {
        List<String> menuItem = orderDetails.getMenuItemList().stream().map(MenuQuantity::getMenuItem).toList();
        List<MenuItem> menuItemList = menuItemsRepository.findByMenuIdAndNameInAndIsDeletedFalse(restaurant.getMenu().getId(), menuItem);
        return orderRepository.save(Order.builder().
                status(OrderStatus.ACCEPTED).
                city(orderDetails.getCity()).
                restaurantId(restaurant.getId()).
                items(menuOrderMapper(orderDetails.getMenuItemList(), menuItemList)).
                customerId(orderDetails.getCustomerId()).
                build());
    }


    public boolean isOrderNotServiceable(String city, List<String> menuItems) throws CustomException {
        List<Restaurant> restaurants = restaurantRepository.findByCity(city);
        // Ensure restaurants and menuItem are not null
        if (restaurants == null || restaurants.isEmpty()) {
            return false;
        }

        Map<Restaurant, List<MenuItem>> menuItemMap = new HashMap<>();
        List<Restaurant> eligibleRestaurants = new ArrayList<>();
        for(Restaurant restaurant: restaurants){
            Menu menu = restaurant.getMenu();
            List<MenuItem> menuItemList = menuItemsRepository.findByMenuIdAndNameInAndIsDeletedFalse(menu.getId(), menuItems);
            if(!menuItemList.isEmpty()) {
                eligibleRestaurants.add(restaurant);
                menuItemMap.put(restaurant, menuItemList);
            }
        }
        return eligibleRestaurants.isEmpty();
    }

}
