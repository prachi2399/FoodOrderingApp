package com.FoodApp.FoodOrderingApp.service.strategy;

import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.dto.MenuDTO;
import com.FoodApp.FoodOrderingApp.entities.Menu;
import com.FoodApp.FoodOrderingApp.entities.Restaurant;

import java.util.List;

public interface RestaurantStrategy {
    Restaurant selectRestaurant(String city, List<String> menuItem) throws CustomException;

}
