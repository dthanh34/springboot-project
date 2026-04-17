package com.example.dto;
import lombok.Data;
@Data

public class User_GoalDTO {
    private Integer id;
    private Long userId; 
    private String goalType;
    private float targetCalories;

}