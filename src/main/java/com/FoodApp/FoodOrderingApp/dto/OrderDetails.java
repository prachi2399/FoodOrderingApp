package com.FoodApp.FoodOrderingApp.dto;

import com.FoodApp.FoodOrderingApp.service.strategy.RestaurantStrategyName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetails {
    private String city;
    private List<MenuQuantity> menuItemList;
    private RestaurantStrategyName restaurantStrategyName;
    private Long customerId;
}
