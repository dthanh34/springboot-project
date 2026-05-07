package com.example.repository;

import com.example.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {
    
    List<Food> findByFoodNameContainingIgnoreCase(String keyword);

    // Đã xóa findByFoodType vì không có cột tương ứng

    @Query(value = "SELECT * FROM food f WHERE f.Food_id NOT IN ( " +
           "  SELECT fi.Food_id FROM food_ingredient fi " +
           "  JOIN user_allergy ua ON fi.Ingredient_id = ua.Ingredient_id " +
           "  WHERE ua.User_id = :userId " +
           "  UNION " +
           "  SELECT fd.Food_id FROM food_disease fd " +
           "  JOIN user_disease ud ON fd.Disease_id = ud.Disease_id " +
           "  WHERE ud.User_id = :userId AND fd.Rating = 1 " +
           ")", nativeQuery = true)
    List<Food> findSafeFoodsForUser(@Param("userId") Long userId);
}