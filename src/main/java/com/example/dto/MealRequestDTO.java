package com.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class MealRequestDTO {
    private Long userId;
    
    private boolean familyMeal; 
    private Double targetCal; 
    private List<Integer> acuteDiseaseIds; 
}