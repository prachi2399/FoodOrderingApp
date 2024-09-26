package com.FoodApp.FoodOrderingApp.repository;

import com.FoodApp.FoodOrderingApp.entities.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemsRepository extends JpaRepository<MenuItem, Long> {
    @Query(nativeQuery = true, value = "select distinct(mi.name) from menu_item as mi join menu as m on m.id = mi.menu_id join restaurant as r on m.id=r.menu_id where r.city=:city and mi.is_deleted=false")
    Page<String> findMenuItemsFromCity(String city, PageRequest pageRequest);

    List<MenuItem> findByMenuId(Long menuId);

    List<MenuItem> findByMenuIdAndNameInAndIsDeletedFalse(Long menuId, List<String> names);

    Optional<MenuItem> findByMenuIdAndName(Long menuId, String name);

    Optional<MenuItem> findFirstByMenuIdAndName(Long menuId, String name);

    Optional<MenuItem> findFirstByMenuIdAndId(Long menuId, Long id);
}
