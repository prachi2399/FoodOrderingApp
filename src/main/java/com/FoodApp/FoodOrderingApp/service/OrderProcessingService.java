package com.FoodApp.FoodOrderingApp.service;

import com.FoodApp.FoodOrderingApp.constants.OrderStatus;
import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.dto.MenuQuantity;
import com.FoodApp.FoodOrderingApp.dto.OrderDetails;
import com.FoodApp.FoodOrderingApp.dto.OrderStatusDTO;
import com.FoodApp.FoodOrderingApp.entities.Order;
import com.FoodApp.FoodOrderingApp.entities.Restaurant;
import com.FoodApp.FoodOrderingApp.repository.OrderRepository;
import com.FoodApp.FoodOrderingApp.repository.RestaurantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


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
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "orders", groupId = "order-group")
    public void consumeOrder(String orderJson) {
        try {
            log.info("consumeOrder {}", orderJson);
            OrderDetails orderDetails = objectMapper.readValue(orderJson, OrderDetails.class);
            processOrder(orderDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "restaurant_orders_update", groupId = "order-restaurant-group")
    public void consumeRestOrderUpdates(String orderJson) {
        try {
            log.info("consumeOrder {}", orderJson);
            OrderStatusDTO orderDetails = objectMapper.readValue(orderJson, OrderStatusDTO.class);
            processOrder(orderDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void processOrder(OrderStatusDTO orderDetails) throws CustomException {
        Order order = orderRepository.findById(orderDetails.getOrderId()).orElse(null);
        order.setStatus(orderDetails.getOrderStatus());
        orderRepository.save(order);
    }

    public void processOrder(OrderDetails orderDetails) throws CustomException {
        // Notify
        // log
    }



}
