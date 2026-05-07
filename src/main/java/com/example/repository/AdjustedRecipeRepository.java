package com.example.repository;

import com.example.entity.AdjustedRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdjustedRecipeRepository extends JpaRepository<AdjustedRecipe, Integer> {
    
    // Tìm công thức tùy chỉnh theo món ăn và người dùng
    Optional<AdjustedRecipe> findByFoodIdAndUserId(Integer foodId, Long userId);
    
    // Kiểm tra xem người dùng đã từng tùy chỉnh món này chưa
    boolean existsByFoodIdAndUserId(Integer foodId, Long userId);
}