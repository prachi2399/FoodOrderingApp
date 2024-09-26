package com.FoodApp.FoodOrderingApp.mappers;

import com.FoodApp.FoodOrderingApp.dto.MenuQuantity;
import com.FoodApp.FoodOrderingApp.entities.MenuItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapper {

    public static Map<Long, Integer> menuOrderMapper(List<MenuQuantity> menuQuantityList, List<MenuItem> menuItems){
       Map<Long, Integer> map = new HashMap<>();
       Map<String, Long> menuNameIdMap = new HashMap<>();
       for(MenuItem menuItem: menuItems){
           menuNameIdMap.put(menuItem.getName(),menuItem.getId());
       }

        for(MenuQuantity menuQuantity: menuQuantityList){
            map.put(menuNameIdMap.get(menuQuantity.getMenuItem()),menuQuantity.getQuantity());
        }
        return map;
    }

}
