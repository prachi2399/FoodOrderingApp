package com.FoodApp.FoodOrderingApp.controller;

import com.FoodApp.FoodOrderingApp.customException.CustomException;
import com.FoodApp.FoodOrderingApp.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@Component
public class MenuController {

    @Autowired
    MenuService menuService;

    @GetMapping("/{city}")
//    @Cacheable(value="menu", key = "#city")
    public ResponseEntity<Page<String>> getMenuFormCity(
            @PathVariable String city,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size)  throws CustomException {
        return ResponseEntity.ok(menuService.getMenuListForCity(city, page, size));
    }

}
