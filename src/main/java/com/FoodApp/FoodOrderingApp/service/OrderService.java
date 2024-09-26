package com.FoodApp.FoodOrderingApp.service;

import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.dto.OrderDetails;
import com.FoodApp.FoodOrderingApp.dto.StandardResponse;

public interface OrderService {

    StandardResponse<String> takeOrder(OrderDetails orderDetails) throws CustomException;


}
