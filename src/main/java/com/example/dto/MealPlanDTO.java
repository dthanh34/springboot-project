package com.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class MealPlanDTO {
    private Long userId; 
    private String date; 
    private Integer mealTypeId; 
    private List<Integer> foodIds; 
}