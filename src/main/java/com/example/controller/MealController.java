package com.example.controller;

import com.example.entity.Food;
import com.example.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
@CrossOrigin("*") // Để Frontend (React/Vue) có thể gọi API mà không bị lỗi CORS
public class MealController {

    @Autowired
    private MealService mealService;

    @GetMapping("/suggest/{userId}")
    public List<Food> getDailySuggestion(@PathVariable Long userId) {
        return mealService.suggestDailyMenu(userId);
    }
}