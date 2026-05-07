package com.example.dto;

import lombok.Data;

@Data
public class IngredientDTO {
    private Integer ingredientId; 
    private String ingredientName;
    private String category;
    private float calories;
    private float protein;
    private float fat;
    private float carbohydrate;
}