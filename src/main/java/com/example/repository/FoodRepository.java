package com.example.repository;

import com.example.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {

    @Query(value = "SELECT f.* FROM Food f " +
       "WHERE f.Food_id NOT IN (" +
       "    SELECT DISTINCT fi.Food_id FROM Food_Ingredient fi " +
       "    JOIN Ingredient_Disease idis ON fi.Ingredient_id = idis.Ingredient_id " +
       "    WHERE idis.Disease_id IN (:allDiseaseIds) AND idis.Is_Kỵ = 1" +
       ") " +
       "AND f.Food_id NOT IN (" +
       "    SELECT DISTINCT fi.Food_id FROM Food_Ingredient fi " +
       "    JOIN User_Allergy ua ON fi.Ingredient_id = ua.Ingredient_id " +
       "    WHERE ua.User_id = :userId" +
       ") " +
       "AND (:dishType IS NULL OR f.dish_type = :dishType)",
       nativeQuery = true)
List<Food> findSafeFoods(
    @Param("allDiseaseIds") List<Integer> allDiseaseIds,
    @Param("userId") Integer userId,
    @Param("dishType") Integer dishType
);
}