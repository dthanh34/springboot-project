package com.example.repository;

import com.example.entity.Ingredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    // Hỗ trợ tìm kiếm phân trang cho Ingredient
    Page<Ingredient> findByIngredientNameContainingIgnoreCase(String keyword, Pageable pageable);
}