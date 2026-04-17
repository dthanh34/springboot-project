package com.example.service;

import com.example.entity.Food;
import com.example.dto.MealRequestDTO;
import com.example.repository.FoodRepository;
import com.example.repository.UserDiseaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class MealService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private UserDiseaseRepository userDiseaseRepo;

    public List<Food> getAllSafeFoods(MealRequestDTO request) {
        // lay danh sach benh cua user
        List<Integer> diseaseIds = userDiseaseRepo.findDiseaseIdsByUserId(request.getUserId());
        if (diseaseIds == null) diseaseIds = new ArrayList<>();
        
        if (diseaseIds.isEmpty() && (request.getAcuteDiseaseIds() == null || request.getAcuteDiseaseIds().isEmpty())) {
            diseaseIds.add(-1); 
        }
        if (request.getAcuteDiseaseIds() != null) {
            diseaseIds.addAll(request.getAcuteDiseaseIds());
        }
   
        return foodRepository.findSafeFoods(diseaseIds, null); // tra  ve danh sach cac mon an an toan
    }
}