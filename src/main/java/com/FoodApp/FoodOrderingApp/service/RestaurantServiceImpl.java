package com.FoodApp.FoodOrderingApp.service;

import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.dto.Address;
import com.FoodApp.FoodOrderingApp.dto.MenuDTO;
import com.FoodApp.FoodOrderingApp.entities.Menu;
import com.FoodApp.FoodOrderingApp.entities.MenuItem;
import com.FoodApp.FoodOrderingApp.entities.Order;
import com.FoodApp.FoodOrderingApp.entities.Restaurant;
import com.FoodApp.FoodOrderingApp.repository.MenuItemsRepository;
import com.FoodApp.FoodOrderingApp.repository.MenuRepository;
import com.FoodApp.FoodOrderingApp.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
@Component
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    MenuItemsRepository menuItemsRepository;

    @Override
    public Restaurant createRestaurant(Restaurant restaurantInput) {
        Menu menu = menuRepository.save(Menu.builder().build());
        Restaurant restaurant =  Restaurant.builder()
                .name(restaurantInput.getName())
                .currentCapacity(0)
                .address(restaurantInput.getAddress())
                .city(restaurantInput.getCity())
                .processingCapacity(restaurantInput.getProcessingCapacity())
                .menu(menu)
                .build();
        return restaurantRepository.save(restaurant);

    }

    @Override
    public Restaurant getRestaurantById(Long id) throws CustomException {
        Restaurant restaurant = restaurantRepository.findById(id).orElse(null);
        if(Objects.isNull(restaurant)){
            throw new CustomException("No Restaurant found with ID "+ id);
        }
        return restaurant;
    }

    @Override
    public void updateRestaurantMenu(Long restaurantId, MenuDTO item) throws CustomException {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new CustomException("Restaurant not found"));
        MenuItem menuItem = MenuItem.builder()
                .menuId(restaurant.getMenu().getId())
                .name(item.getName())
                .price(item.getPrice())
                .isDeleted(false)
                .build();
        menuItemsRepository.save(menuItem);
    }

    @Override
    public void deleteRestaurantMenu(Long restId, String menuName) throws CustomException {
        Restaurant restaurant = restaurantRepository.findById(restId).orElseThrow(()->new CustomException("No resturant exist with id "+ restId));
        MenuItem menuItem = menuItemsRepository.findFirstByMenuIdAndName(restaurant.getMenu().getId(), menuName).orElseThrow(()->new CustomException("No menu exist with name "+ menuName));
        menuItem.setDeleted(true);
        menuItemsRepository.save(menuItem);
    }
    @Override
    public Boolean isOrderDeliverable(String city, Address Address, List<Menu> menuItems) throws CustomException {
        List<Restaurant> restaurants = restaurantRepository.findByCity(city);

        // Ensure restaurants and menuItem are not null
        if (restaurants == null || restaurants.isEmpty()) {
            throw new CustomException("No restaurants available to deliver in the city" +city);
        }

        // Filter restaurants offering the menuItem
//        List<Restaurant> eligibleRestaurants = restaurants.stream()
//                .filter(restaurant -> restaurant.getMenuItems().containsAll(menuItems))
//                .collect(Collectors.toList());
//
//        // If no eligible restaurants found, throw an exception
//        if (eligibleRestaurants.isEmpty()) {
//            throw new CustomException("No restaurants offering the menuItem found.");
//        }
        return true;
    }

    @Override
    public void updateRestaurantCapacity(Long restaurantId, int currentCapacity) throws CustomException {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);
        if(Objects.isNull(restaurant)){
            throw new CustomException("No Restaurant found with given id" + restaurantId);
        }
        restaurant.setCurrentCapacity(currentCapacity);
        restaurantRepository.save(restaurant);
    }

}
