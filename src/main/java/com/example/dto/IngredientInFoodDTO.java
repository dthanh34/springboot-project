package com.example.dto;

import lombok.Data;

@Data
public class IngredientInFoodDTO {
    private Integer ingredientId;
    private String ingredientName;

    private Double quantity; // từ FoodIngredient
    private String unit;     // từ FoodIngredient
}
