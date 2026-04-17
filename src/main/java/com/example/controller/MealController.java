package com.example.controller;

import com.example.dto.MealRequestDTO;
import com.example.entity.Food;
import com.example.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    @Autowired
    private MealService mealService;

    @PostMapping("/generate") // api lay cac mon phu hop
    public List<Food> generateMeal(@RequestBody MealRequestDTO request) {
        return mealService.getAllSafeFoods(request);
    }
}