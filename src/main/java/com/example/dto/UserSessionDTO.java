package com.example.dto;
import lombok.Data;
@Data
public class UserSessionDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private Integer age;    
    private float weight;  
    private float height;
    private Boolean gender; 
    private Double activityLevel;
}