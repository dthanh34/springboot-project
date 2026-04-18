package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyMenuResponseDTO {
    private String foodName;
    private Double calories;
    private String imageUrl;
    private Integer mealTypeId; 
}