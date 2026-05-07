package com.example.dto;

import lombok.Data;

@Data
public class HealthProgressDTO {
    private float currentWeight; 
    private float currentHeight; 
    private double bmi;
    private double progressPercent; 
    private double heightProgressPercent; 
}