package com.FoodApp.FoodOrderingApp.dto;

import com.FoodApp.FoodOrderingApp.constants.OrderStatus;
import com.FoodApp.FoodOrderingApp.entities.Order;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class OrderStatusDTO {
    private Long orderId;
    private OrderStatus orderStatus;

}
