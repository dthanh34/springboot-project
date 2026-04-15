package com.example.controller;

import com.example.dto.MealRequestDTO;
import com.example.entity.Food;
import com.example.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
@CrossOrigin("*") // Để Frontend (React/Vue) có thể gọi API mà không bị lỗi CORS
public class MealController {

    @Autowired
    private MealService mealService;

    

    @PostMapping("/generate")
    public ResponseEntity<?> generateMeal(@RequestBody MealRequestDTO request) {
        List<Food> menu = mealService.generateMenu(request);
        return ResponseEntity.ok(menu);
    }
    

}