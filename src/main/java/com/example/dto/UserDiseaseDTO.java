package com.example.dto;

import lombok.Data;

@Data
public class UserDiseaseDTO {
    private Integer id;
    private Long userId;
    private Integer diseaseId;
    private String diseaseName;

    private String level;
    private String discoveryDate;
    private String note;
}