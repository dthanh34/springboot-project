package com.example.dto;

import lombok.Data;

@Data
public class ProgressDTO {

    private Float todayCalories;
    private Float currentWeight;
    private Integer totalDaysFollowed;
    private String goalLabel;
    private Float bmi;

    private Float currentHeight;

    private Float weightProgressPercent;
    private Float targetWeight;

    private Float heightProgressPercent;
    private Float targetHeight;
}