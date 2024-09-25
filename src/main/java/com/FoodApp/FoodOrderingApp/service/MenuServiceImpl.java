package com.FoodApp.FoodOrderingApp.service;

import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.entities.MenuItem;
import com.FoodApp.FoodOrderingApp.repository.MenuItemsRepository;
import com.FoodApp.FoodOrderingApp.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MenuServiceImpl implements MenuService {
    @Autowired
    MenuItemsRepository menuItemsRepository;

    @Override
    public List<String> getMenuListForCity(String city) throws CustomException {
        List<String> menu =  menuItemsRepository.findMenuItemsFromCity(city);

        if(menu.isEmpty()){
            throw new CustomException("No Restaurants serviceable in your city" + city);
        }
        return menu;
    }
}
