package com.FoodApp.FoodOrderingApp.entities;

import com.FoodApp.FoodOrderingApp.dto.Address;
import com.FoodApp.FoodOrderingApp.dto.AddressConveerter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Table(name="customer_db")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String contactNumber;

    @Convert(converter = AddressConveerter.class)
    private Address address;

    private String city;
}
