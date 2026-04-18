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
           "    /*  Lấy các món chứa nguyên liệu kỵ với bệnh của User */ " +
           "    SELECT DISTINCT fi.Food_id FROM Food_Ingredient fi " +
           "    JOIN Ingredient_Disease idis ON fi.Ingredient_id = idis.Ingredient_id " +
           "    WHERE idis.Disease_id IN (:allDiseaseIds) AND idis.Is_Kỵ = 1 " +
           "    UNION " +
           "    /*  Lấy các món chứa nguyên liệu mà User bị dị ứng */ " +
           "    SELECT DISTINCT fi.Food_id FROM Food_Ingredient fi " +
           "    WHERE fi.Ingredient_id IN (:allAllergyIngredientIds)" +
           ") " +
           "AND (:dishType IS NULL OR f.dish_type = :dishType) " +
           "AND (:isBreakfast IS NULL OR f.is_breakfast = :isBreakfast)", nativeQuery = true)
    List<Food> findSafeFoods(@Param("allDiseaseIds") List<Integer> allDiseaseIds, 
                             @Param("allAllergyIngredientIds") List<Integer> allAllergyIngredientIds,
                             @Param("dishType") Integer dishType,
                             @Param("isBreakfast") Boolean isBreakfast);

    @Query(value = "SELECT TOP 1 f.* FROM Food f " +
           "WHERE f.dish_type = 0 " +
           "AND f.calories <= :targetCal " +
           "AND f.Food_id NOT IN (" +
           "    SELECT DISTINCT fi.Food_id FROM Food_Ingredient fi " +
           "    JOIN Ingredient_Disease idis ON fi.Ingredient_id = idis.Ingredient_id " +
           "    WHERE idis.Disease_id IN (:allDiseaseIds) AND idis.Is_Kỵ = 1 " +
           "    UNION " +
           "    SELECT DISTINCT fi.Food_id FROM Food_Ingredient fi " +
           "    WHERE fi.Ingredient_id IN (:allAllergyIngredientIds)" +
           ") " +
           "ORDER BY NEWID()", nativeQuery = true)
    Food findRandomSafeEatOut(@Param("allDiseaseIds") List<Integer> allDiseaseIds, 
                              @Param("allAllergyIngredientIds") List<Integer> allAllergyIngredientIds,
                              @Param("targetCal") Double targetCal);

    @Query(value = "SELECT f.* FROM Food f " +
           "WHERE f.dish_type = :dishType " +
           "AND f.Food_id NOT IN (" +
           "    SELECT DISTINCT fi.Food_id FROM Food_Ingredient fi " +
           "    JOIN Ingredient_Disease idis ON fi.Ingredient_id = idis.Ingredient_id " +
           "    WHERE idis.Disease_id IN (:allDiseaseIds) AND idis.Is_Kỵ = 1 " +
           "    UNION " +
           "    SELECT DISTINCT fi.Food_id FROM Food_Ingredient fi " +
           "    WHERE fi.Ingredient_id IN (:allAllergyIngredientIds)" +
           ")", nativeQuery = true)
    List<Food> findSafeFoodsByType(@Param("allDiseaseIds") List<Integer> allDiseaseIds, 
                                   @Param("allAllergyIngredientIds") List<Integer> allAllergyIngredientIds,
                                   @Param("dishType") Integer dishType);
}