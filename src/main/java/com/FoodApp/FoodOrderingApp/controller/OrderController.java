package com.FoodApp.FoodOrderingApp.controller;

import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.dto.OrderDetails;
import com.FoodApp.FoodOrderingApp.dto.StandardResponse;
import com.FoodApp.FoodOrderingApp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/")
    public ResponseEntity<StandardResponse<String>> placeOrder(@RequestBody OrderDetails orderDetails) throws CustomException {
        StandardResponse<String> order = orderService.takeOrder(orderDetails);
        return ResponseEntity.ok(order);
    }

}
