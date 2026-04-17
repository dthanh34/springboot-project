package com.example.dto;
import lombok.Data;
@Data

public class UserAllergyDTO {
    private Integer id;
    private Long userId;
    private Integer ingredientId;
    private String ingredientName;
    private String reaction;
    private String note;
}