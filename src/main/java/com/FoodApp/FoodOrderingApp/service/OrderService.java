package com.FoodApp.FoodOrderingApp.service;

import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.dto.OrderDetails;
import com.FoodApp.FoodOrderingApp.entities.Order;
import com.FoodApp.FoodOrderingApp.entities.Restaurant;

public interface OrderService {

    String takeOrder(OrderDetails orderDetails) throws CustomException;

    void completeOrder(Long order) throws CustomException;

}
