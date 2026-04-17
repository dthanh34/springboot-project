package com.example.dto; 

import lombok.Data;
import java.util.List;

@Data
public class UserRegisterDTO {
    private String name;
    private String email;
    private String password;

    private Boolean gender;
    private Integer age;
    private Double height;
    private Double weight;
    private Double activityLevel; 

    private Double desiredHeight;
    private Double desiredWeight;
    private String goalType;

    private List<Integer> diseaseIds;
    private List<Integer> ingredientIds;
}