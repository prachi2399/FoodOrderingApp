package com.FoodApp.FoodOrderingApp.repository;

import com.FoodApp.FoodOrderingApp.entities.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemsRepository extends JpaRepository<MenuItem, Long> {
    @Query(nativeQuery = true, value = "select distinct(mi.name) from menu_item as mi join menu as m on m.id = mi.menu_id join restaurant as r on m.id=r.menu_id where r.city=:city")
    List<String> findMenuItemsFromCity(String city);

//    @Query(nativeQuery = true, value = "select * from menu_item where menu_id=:menuId")
    List<MenuItem> findByMenuId(Long menuId);

//    @Query(nativeQuery = true, value = "select * from menu_item where menu_id=:menuId and menu_item.name in:names")
    List<MenuItem> findByMenuIdAndNameIn(Long menuId, List<String> names);

    Optional<MenuItem> findByMenuIdAndName(Long menuId, String name);

    Optional<MenuItem> findFirstByMenuIdAndName(Long menuId, String name);
}
