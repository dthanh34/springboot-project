package com.example.repository;

import com.example.entity.FoodDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodDiseaseRepository extends JpaRepository<FoodDisease, Integer> {
    // Kiểm tra tương thích món ăn - bệnh lý
    boolean existsByFoodFoodIdAndDiseaseDiseaseId(Integer foodId, Integer diseaseId);
    void deleteByDiseaseDiseaseId(Integer diseaseId);
}