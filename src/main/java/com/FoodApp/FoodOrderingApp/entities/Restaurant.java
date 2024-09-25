package com.FoodApp.FoodOrderingApp.entities;

import com.FoodApp.FoodOrderingApp.dto.Address;
import com.FoodApp.FoodOrderingApp.dto.AddressConveerter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import lombok.*;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Table(name = "restaurant")
//@TypeDef(
//typeClass = JsonBinaryType.class)
@Transactional
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String city;

    @Convert(converter = AddressConveerter.class)
    private Address address;

    @NonNull
    private int processingCapacity;

    private int currentCapacity;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Menu menu;

//    private boolean isServiceAble=true;

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
    public void addMenuItemToMenu(MenuItem menuItem) {
        menu.addMenuItem(menuItem);
    }

    public void removeMenuItemFromMenu(MenuItem menuItem) {
        menu.removeMenuItem(menuItem);
    }
}
