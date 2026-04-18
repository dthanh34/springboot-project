package com.example.repository;

import com.example.entity.MenuDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuDetailRepository extends JpaRepository<MenuDetail, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM MenuDetail md WHERE md.menuId = :menuId")
    void deleteByMenuId(@Param("menuId") Integer menuId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Menu_Detail (Menu_id, Food_id, Meal_type_id) VALUES (:menuId, :foodId, :mealTypeId)", nativeQuery = true)
    void insertDetail(@Param("menuId") Integer menuId, @Param("foodId") Integer foodId, @Param("mealTypeId") Integer mealTypeId);
}