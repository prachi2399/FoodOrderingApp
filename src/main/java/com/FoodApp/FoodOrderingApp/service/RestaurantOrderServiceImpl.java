package com.FoodApp.FoodOrderingApp.service;

import com.FoodApp.FoodOrderingApp.constants.OrderStatus;
import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.dto.OrderStatusDTO;
import com.FoodApp.FoodOrderingApp.dto.StandardResponse;
import com.FoodApp.FoodOrderingApp.entities.Order;
import com.FoodApp.FoodOrderingApp.entities.Restaurant;
import com.FoodApp.FoodOrderingApp.repository.OrderRepository;
import com.FoodApp.FoodOrderingApp.repository.RestaurantRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Component
public class RestaurantOrderServiceImpl {

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TOPIC = "restaurant_orders_update";


    public StandardResponse<String> completeOrder(long restId, long orderId) throws CustomException, JsonProcessingException {
        StandardResponse<String> standardResponse = new StandardResponse<>();
        Restaurant restaurant = restaurantRepository.findById(restId).orElse(null);
        if(Objects.isNull(restaurant)){
            standardResponse.setMessage("No restaurant present");
            standardResponse.setError("No restaurant present");
            standardResponse.setSuccess(false);
            return standardResponse;
        }

        if(restaurant.getCurrentCapacity()>0){
            restaurant.setCurrentCapacity(restaurant.getCurrentCapacity()-1);
            restaurantRepository.save(restaurant);
        }

        OrderStatusDTO orderStatusDTO = OrderStatusDTO.builder()
                .orderId(orderId)
                .orderStatus(OrderStatus.DISPATCHED)
                .build();

        String orderJson= objectMapper.writeValueAsString(orderStatusDTO);
        kafkaTemplate.send(TOPIC, orderJson);

//        Order order = orderRepository.findById(orderId).orElse(null);
//        if(Objects.isNull(order)){
//            standardResponse.setMessage("No order present");
//            standardResponse.setError("No order present");
//            standardResponse.setSuccess(false);
//            return standardResponse;
//        }
//        order.setStatus(OrderStatus.DISPATCHED);
//        orderRepository.save(order);
        standardResponse.setMessage("order dispacted successfully");
        standardResponse.setSuccess(true);
        standardResponse.setData(OrderStatus.DISPATCHED.toString());
        return standardResponse;
    }

}
