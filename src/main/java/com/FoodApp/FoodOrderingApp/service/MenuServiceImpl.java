package com.FoodApp.FoodOrderingApp.service;

import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.entities.MenuItem;
import com.FoodApp.FoodOrderingApp.repository.MenuItemsRepository;
import com.FoodApp.FoodOrderingApp.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MenuServiceImpl implements MenuService {
    @Autowired
    MenuItemsRepository menuItemsRepository;

    @Override
    public Page<String> getMenuListForCity(String city, int offset, int pageSize) throws CustomException {
        Page<String> menu =  menuItemsRepository.findMenuItemsFromCity(city, PageRequest.of(offset, pageSize));

        if(menu.isEmpty()){
            throw new CustomException("No Restaurants serviceable in your city" + city);
        }
        return menu;
    }
}
