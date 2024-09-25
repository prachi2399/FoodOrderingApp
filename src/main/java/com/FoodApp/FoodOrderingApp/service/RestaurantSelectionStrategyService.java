package com.FoodApp.FoodOrderingApp.service;

import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.dto.MenuDTO;
import com.FoodApp.FoodOrderingApp.entities.Menu;
import com.FoodApp.FoodOrderingApp.entities.Restaurant;
import com.FoodApp.FoodOrderingApp.service.strategy.RestaurantStrategyName;
import com.FoodApp.FoodOrderingApp.service.strategy.RestaurantStrategy;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class RestaurantSelectionStrategyService {
    @Autowired
    private Map<String, RestaurantStrategy> restaurantStrategy;

    public Restaurant selectRestaurant(String city, List<String> menuItem, RestaurantStrategyName restaurantStrategyName) throws CustomException {
        RestaurantStrategy strategy = restaurantStrategy.get(restaurantStrategyName.toString());
        if(Objects.isNull(strategy)) throw new IllegalArgumentException("No Strategy present for strategy name "+ restaurantStrategyName.toString());
        return strategy.selectRestaurant(city, menuItem);
    }
}
