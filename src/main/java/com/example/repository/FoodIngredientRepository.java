package com.example.repository;

import com.example.entity.FoodIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodIngredientRepository extends JpaRepository<FoodIngredient, Integer> {
    List<FoodIngredient> findByFood_FoodId(Integer foodId);
    
    void deleteByFoodFoodId(Integer foodId);
    
}