package com.FoodApp.FoodOrderingApp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @JsonIgnore
    private String address;

    private int pincode;

    private double latitude;

    private double longitude;
}
