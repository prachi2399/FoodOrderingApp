package com.FoodApp.FoodOrderingApp.service;

import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.dto.Address;
import com.FoodApp.FoodOrderingApp.dto.MenuDTO;
import com.FoodApp.FoodOrderingApp.entities.Menu;
import com.FoodApp.FoodOrderingApp.entities.MenuItem;
import com.FoodApp.FoodOrderingApp.entities.Restaurant;

import java.util.List;


public interface RestaurantService {
    Restaurant createRestaurant(Restaurant restaurant);

    Restaurant getRestaurantById(Long id) throws CustomException;

    void updateRestaurantMenu(Long restaurantId, MenuDTO menuItem) throws CustomException;

    void deleteRestaurantMenu(Long restaurantId, String menuName) throws CustomException;

    void updateRestaurantCapacity(Long restaurantId, int currentCapacity) throws CustomException;

    void decreaseRestaurantCapacity(Long restaurantId) throws CustomException;

}
