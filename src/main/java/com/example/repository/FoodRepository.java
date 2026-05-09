package com.example.repository;

import com.example.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {
    // Tìm kiếm món ăn theo tên (Ánh xạ cột Food_name)
    List<Food> findByFoodNameContainingIgnoreCase(String keyword);

    // Lấy danh sách món ăn theo loại (Sáng, Trưa, Tối hoặc loại món)
    List<Food> findByFoodType(Integer foodType);

    @Query(value = "SELECT * FROM food f WHERE f.Food_id NOT IN ( " +
           "SELECT fi.Food_id FROM food_ingredient fi JOIN user_allergy ua ON fi.Ingredient_id = ua.Ingredient_id WHERE ua.User_id = :userId " +
           "UNION " +
           "SELECT fi.Food_id FROM food_ingredient fi JOIN ingredient_disease idist ON fi.Ingredient_id = idist.Ingredient_id " +
           "JOIN user_disease ud ON idist.Disease_id = ud.Disease_id WHERE ud.User_id = :userId AND idist.Is_Ky = 1 " +
           "UNION " +
           "SELECT fd.Food_id FROM food_disease fd JOIN user_disease ud ON fd.Disease_id = ud.Disease_id WHERE ud.User_id = :userId AND fd.Rating = 1 " +
           ")", nativeQuery = true)
    List<Food> findSafeFoodsForUser(@Param("userId") Long userId);
 @Query(value = "SELECT COUNT(*) FROM food WHERE create_at >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')", nativeQuery = true)
    long countFoodsThisMonth();

    @Query(value = "SELECT COUNT(*) FROM food WHERE create_at >= DATE_FORMAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH), '%Y-%m-01') AND create_at < DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')", nativeQuery = true)
    long countFoodsLastMonth();
}