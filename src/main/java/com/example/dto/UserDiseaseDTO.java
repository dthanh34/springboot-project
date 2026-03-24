package com.example.dto;

import lombok.Data;

@Data
public class UserDiseaseDTO {
    private Integer id;
    private Long userId;
    private Integer diseaseId;
    private String diseaseName;
}