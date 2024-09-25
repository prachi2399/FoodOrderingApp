package com.FoodApp.FoodOrderingApp.entities;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Table(name = "menu")
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Inheritance(strategy = InheritanceType.JOINED)
public class Menu{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


//    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
//    private List<MenuItem> menuItems = new ArrayList<>();

    // Getters and setters

//    @Override
    public void addMenuItem(MenuItem menuItem) {
//        menuItems.add(menuItem);

//        menuItem.setMenu(this); // Set the parent-child relationship
    }

//    @Override
    public void removeMenuItem(MenuItem menuItem) {
//        menuItems.remove(menuItem);
//        menuItem.setMenu(null); // Remove the parent-child relationship
    }

}
