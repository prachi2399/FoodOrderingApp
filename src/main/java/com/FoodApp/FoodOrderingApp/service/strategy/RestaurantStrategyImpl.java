package com.FoodApp.FoodOrderingApp.service.strategy;

import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.dto.MenuDTO;
import com.FoodApp.FoodOrderingApp.entities.Menu;
import com.FoodApp.FoodOrderingApp.entities.MenuItem;
import com.FoodApp.FoodOrderingApp.entities.Restaurant;
import com.FoodApp.FoodOrderingApp.repository.MenuItemsRepository;
import com.FoodApp.FoodOrderingApp.repository.MenuRepository;
import com.FoodApp.FoodOrderingApp.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component("LOWEST_PRICE")
public class RestaurantStrategyImpl implements RestaurantStrategy{
    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    MenuItemsRepository menuItemRepository;

    public Restaurant selectRestaurant(String city, List<String> menuItems) throws CustomException {

        List<Restaurant> restaurants = restaurantRepository.findByCity(city);
//        List<String> menuItemNames = menuItems.stream().map(MenuDTO::getName).toList();
        // Ensure restaurants and menuItem are not null
        if (restaurants == null || restaurants.isEmpty() || menuItems.isEmpty()) {
            throw new CustomException("No restaurants available to deliver in the city" +city);
        }

        Map<Restaurant, List<MenuItem>> menuItemMap = new HashMap<>();
        List<Restaurant> eligibleRestaurants = new ArrayList<>();
        for(Restaurant restaurant: restaurants){
            Menu menu = restaurant.getMenu();
            List<MenuItem> menuItemList = menuItemRepository.findByMenuIdAndNameIn(menu.getId(), menuItems);
            if(!menuItemList.isEmpty() && restaurant.getCurrentCapacity()< restaurant.getProcessingCapacity()) {
                eligibleRestaurants.add(restaurant);
                menuItemMap.put(restaurant, menuItemList);
            }
        }

        // Filter restaurants offering the menuItem
//                restaurants.stream()
//                .filter(restaurant -> restaurant.getMenuItems().containsAll(menuItems))
//                .collect(Collectors.toList());

        // If no eligible restaurants found, throw an exception
        if (menuItemMap.isEmpty() || eligibleRestaurants.isEmpty()) {
            throw new CustomException("No restaurants offering the menuItem found.");
        }

        // Find the restaurant with the lowest price for the menuItem
        Restaurant lowestPriceRestaurant = new Restaurant();
        lowestPriceRestaurant  = eligibleRestaurants.stream()
                .min(Comparator.comparing(restaurant -> calculateTotalPrice(menuItemMap.get(restaurant))))
                .orElse(null);

        return lowestPriceRestaurant;
    }

    private BigDecimal calculateTotalPrice(List<MenuItem> menuItemList) {
        return menuItemList.stream()
                .map(MenuItem::getPrice)  // Extract the price from each MenuItem
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
