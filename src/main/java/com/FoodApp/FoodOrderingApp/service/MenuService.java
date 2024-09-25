package com.FoodApp.FoodOrderingApp.service;

import com.FoodApp.FoodOrderingApp.customException.CustomException;

import java.util.List;

public interface MenuService {
    List<String> getMenuListForCity(String city) throws CustomException;
}
