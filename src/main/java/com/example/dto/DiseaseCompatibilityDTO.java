package com.example.dto;

import lombok.Data;

@Data
public class DiseaseCompatibilityDTO {
    private Integer foodId;
    private Integer diseaseId;
    private Integer rating; 
}