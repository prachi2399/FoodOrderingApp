package com.FoodApp.FoodOrderingApp.service;

import com.FoodApp.FoodOrderingApp.customException.CustomException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MenuService {
    Page<String> getMenuListForCity(String city, int page, int size) throws CustomException;
}
