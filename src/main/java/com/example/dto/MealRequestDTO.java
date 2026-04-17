package com.example.dto;
import lombok.Data;
import java.util.List;
@Data
public class MealRequestDTO {
    private Long userId;
    private boolean isFamilyMeal;
    private Double tdee;
    private List<Integer> acuteDiseaseIds; 
    
}
