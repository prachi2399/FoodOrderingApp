package com.FoodApp.FoodOrderingApp.controller;
import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.dto.StandardResponse;
import com.FoodApp.FoodOrderingApp.service.RestaurantOrderServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/restaurantOrder")
public class RestuarantOrderController {

    @Autowired
    RestaurantOrderServiceImpl restaurantOrderService;

    @PatchMapping("{restId}/order/{orderId}")
    public ResponseEntity completeOrderStatus(@PathVariable Long restId, @PathVariable Long orderId) throws CustomException, JsonProcessingException {
        StandardResponse<String> standardResponse = restaurantOrderService.completeOrder(restId, orderId);
        if(Objects.isNull(standardResponse.getError())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(standardResponse);
        return ResponseEntity.ok(standardResponse);

    }
}
