package com.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class MealSectionDTO {
    private Integer mealTypeId;
    private String mealName;
    private double targetCalories;
    private double usedCalories;
    private List<FoodSummaryDTO> foods; 
}

@Data
class FoodSummaryDTO {
    private Integer foodId;
    private String foodName;
    private String imageUrl;
    private double calories;
}