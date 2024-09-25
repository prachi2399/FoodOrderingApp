package com.FoodApp.FoodOrderingApp.repository;

import com.FoodApp.FoodOrderingApp.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
