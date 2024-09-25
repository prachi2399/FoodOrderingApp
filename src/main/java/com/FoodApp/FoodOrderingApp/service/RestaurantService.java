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

    void deleteRestaurantMenu(Long menuId, String menuName) throws CustomException;

    Boolean isOrderDeliverable(String city, Address Address, List<Menu> menuList) throws CustomException;
    void updateRestaurantCapacity(Long restaurantId, int currentCapacity) throws CustomException;
}
