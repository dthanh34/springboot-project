package com.example.service;

import com.example.entity.Disease;
import com.example.entity.FoodDisease;
import com.example.repository.DiseaseRepository;
import com.example.repository.FoodDiseaseRepository;
import com.example.repository.FoodRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiseaseService {

    @Autowired private DiseaseRepository diseaseRepo;
    @Autowired private FoodDiseaseRepository foodDiseaseRepo;
    @Autowired private FoodRepository foodRepo;

    @Transactional
    public void addFoodCompatibility(Integer foodId, Integer diseaseId, Integer rating) {
        // Kiểm tra xem đã tồn tại chưa để tránh trùng lặp 
        if (!foodDiseaseRepo.existsByFoodFoodIdAndDiseaseDiseaseId(foodId, diseaseId)) {
            FoodDisease fd = new FoodDisease();
            foodRepo.findById(foodId).ifPresent(fd::setFood);
            diseaseRepo.findById(diseaseId).ifPresent(fd::setDisease);
            fd.setRating(rating);
            foodDiseaseRepo.save(fd);
        }
    }

    public List<Disease> getAllDiseases() {
        return diseaseRepo.findAll();
    }

    public List<Map<String, Object>> getAllCompatibility() {
         return new ArrayList<>(); 
    }
}