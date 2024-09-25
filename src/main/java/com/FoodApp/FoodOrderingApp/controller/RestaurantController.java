package com.FoodApp.FoodOrderingApp.controller;

import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.dto.MenuDTO;
import com.FoodApp.FoodOrderingApp.dto.MenuItemDTO;
import com.FoodApp.FoodOrderingApp.entities.Menu;
import com.FoodApp.FoodOrderingApp.entities.Restaurant;
import com.FoodApp.FoodOrderingApp.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @PostMapping("/")
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        Restaurant createdRestaurant = restaurantService.createRestaurant(restaurant);
        return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) throws CustomException {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);

    }

    @PatchMapping("/{id}/menu/")
    public ResponseEntity<String> updateRestaurantMenu(@PathVariable Long id, @RequestBody MenuDTO menuItem) throws CustomException {
        restaurantService.updateRestaurantMenu(id, menuItem);
        return ResponseEntity.status(HttpStatus.CREATED).body("Menu added successfully");
    }

    @DeleteMapping("/{id}/menu/{name}")
    public ResponseEntity<String> deleteRestaurantMenu(@PathVariable Long id, @PathVariable String name) throws CustomException {
        restaurantService.deleteRestaurantMenu(id, name);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Menu deleted successfully");
    }

}

