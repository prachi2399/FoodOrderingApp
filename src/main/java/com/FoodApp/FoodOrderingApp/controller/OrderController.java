package com.FoodApp.FoodOrderingApp.controller;

import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.dto.OrderDetails;
import com.FoodApp.FoodOrderingApp.entities.Order;
import com.FoodApp.FoodOrderingApp.entities.Restaurant;
import com.FoodApp.FoodOrderingApp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/")
    public ResponseEntity<String> placeOrder(@RequestBody OrderDetails orderDetails) throws CustomException {
        return ResponseEntity.ok(orderService.takeOrder(orderDetails));
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long orderId) throws CustomException {
        orderService.completeOrder(orderId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Menu deleted successfully");
    }
}
