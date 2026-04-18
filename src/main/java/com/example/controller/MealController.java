package com.example.controller;

import com.example.dto.DailyMenuRequestDTO;
import com.example.dto.DailyMenuResponseDTO;
import com.example.dto.MealRequestDTO;
import com.example.entity.Food;
import com.example.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    @Autowired
    private MealService mealService;
    @PostMapping("/suggest")
    public ResponseEntity<List<Food>> suggestMeal(@RequestBody MealRequestDTO request) {
        List<Food> suggestions = mealService.suggestMeal(
                request.getUserId(), 
                !request.isFamilyMeal(), 
                request.getTargetCal(), 
                request.getAcuteDiseaseIds()
        );
        
        if (suggestions == null || suggestions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(suggestions);
    }

    @PostMapping("/all-safe")
    public ResponseEntity<List<Food>> getAllSafeFoods(@RequestBody MealRequestDTO request) {
        List<Food> safeFoods = mealService.getAllSafeFoods(request);
        return ResponseEntity.ok(safeFoods);
    }

    @PostMapping("/save-daily-menu")
    public ResponseEntity<?> saveDailyMenu(@RequestBody DailyMenuRequestDTO request) {
        try {
            mealService.saveUserDailyMenu(request);
            return ResponseEntity.ok().body(Map.of("message", "Lưu thực đơn thành công!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Lỗi: " + e.getMessage()));
        }
    }
    
    @GetMapping("/get-daily-menu")
    public ResponseEntity<?> getDailyMenu(@RequestParam Long userId, @RequestParam String date) {
        try {
            List<DailyMenuResponseDTO> data = mealService.getMenuByDate(userId, date);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
             return ResponseEntity.status(500).body("Lỗi Backend: " + e.getMessage());
        }
    }
}