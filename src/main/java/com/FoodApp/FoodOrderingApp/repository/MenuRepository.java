package com.FoodApp.FoodOrderingApp.repository;

import com.FoodApp.FoodOrderingApp.entities.Menu;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//@RepositoryRestResource
@Repository
@Transactional
public interface MenuRepository extends JpaRepository<Menu,Long> {
}
