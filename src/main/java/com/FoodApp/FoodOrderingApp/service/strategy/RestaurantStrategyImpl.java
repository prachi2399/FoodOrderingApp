package com.FoodApp.FoodOrderingApp.service.strategy;

import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.entities.Menu;
import com.FoodApp.FoodOrderingApp.entities.MenuItem;
import com.FoodApp.FoodOrderingApp.entities.Restaurant;
import com.FoodApp.FoodOrderingApp.repository.MenuItemsRepository;
import com.FoodApp.FoodOrderingApp.repository.MenuRepository;
import com.FoodApp.FoodOrderingApp.repository.RestaurantRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component("LOWEST_PRICE")
@Log4j2
public class RestaurantStrategyImpl implements RestaurantStrategy{
    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    MenuItemsRepository menuItemRepository;

    public Restaurant selectRestaurant(String city, List<String> menuItems) throws CustomException {
        if(menuItems.isEmpty()){
            log.error("Invalid details provided");
            throw new CustomException("valid details not provided");
        }

        List<Restaurant> restaurants = restaurantRepository.findByCity(city);
        // Ensure restaurants and menuItem are not null
        if (restaurants == null || restaurants.isEmpty()) {
            log.info("No restaurant in the city to accept Order");
            return null;
        }

        Map<Restaurant, List<MenuItem>> menuItemMap = new HashMap<>();
        List<Restaurant> eligibleRestaurants = new ArrayList<>();
        for(Restaurant restaurant: restaurants){
            Menu menu = restaurant.getMenu();
            List<MenuItem> menuItemList = menuItemRepository.findByMenuIdAndNameInAndIsDeletedFalse(menu.getId(), menuItems);
            if(!menuItemList.isEmpty() && restaurant.getCurrentCapacity()< restaurant.getProcessingCapacity()) {
                eligibleRestaurants.add(restaurant);
                menuItemMap.put(restaurant, menuItemList);
            }
        }

        // If no eligible restaurants found, throw an exception
        if (menuItemMap.isEmpty() || eligibleRestaurants.isEmpty()) {
            log.info("no restaurant present to accept this order");
            return null;
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
