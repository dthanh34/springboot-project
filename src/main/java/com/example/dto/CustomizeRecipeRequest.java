package com.example.dto;

import lombok.Data;

@Data
public class CustomizeRecipeRequest {
    private Integer foodId;
    private String recipeText; 
    private float calculatedCalories;
    private float calculatedFat;
    private float calculatedProtein;
    private float calculatedCarbs;
}