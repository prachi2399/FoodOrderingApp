package com.FoodApp.FoodOrderingApp.entities;

import com.FoodApp.FoodOrderingApp.constants.OrderStatus;
import com.FoodApp.FoodOrderingApp.dto.MenuQuantity;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Table(name="order_db")
@Transactional
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;

//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Restaurant restaurantSelected;

    private int EstimatedDeliveryTime;

    private String city;

    @ElementCollection
    private Map<Long, Integer> items; // (List of ItemQuantity pairs)
}
