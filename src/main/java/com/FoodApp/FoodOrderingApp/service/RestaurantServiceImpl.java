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
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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

    @Autowired
    EntityManager entityManager;


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
    @Transactional
    public Restaurant getRestaurantById(Long id) throws CustomException {
        Restaurant restaurant = restaurantRepository.findById(id).orElse(null);
        restaurantRepository.save(restaurant);
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
    public void deleteRestaurantMenu(Long restId, String name) throws CustomException {
        Restaurant restaurant = restaurantRepository.findById(restId).orElseThrow(()->new CustomException("No resturant exist with id "+ restId));
        MenuItem menuItem = menuItemsRepository.findFirstByMenuIdAndName(restaurant.getMenu().getId(), name).orElse(null);
        if(Objects.isNull(menuItem)){
            throw new CustomException("No menu item found");
        }
        menuItem.setDeleted(true);
        menuItemsRepository.save(menuItem);
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

    @Override
    public void decreaseRestaurantCapacity(Long restaurantId) throws CustomException {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);

        if(Objects.isNull(restaurant)){
            throw new CustomException("No Restaurant found with given id" + restaurantId);
        }
        if(restaurant.getCurrentCapacity()>0) restaurant.setCurrentCapacity(restaurant.getCurrentCapacity()-1);
        restaurantRepository.save(restaurant);
    }

}
