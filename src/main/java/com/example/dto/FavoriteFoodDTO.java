package com.example.dto;

import lombok.Data;

@Data
public class FavoriteFoodDTO {
    private Integer foodId;
    private String foodName;
    private String imageUrl;
    private Double calories;
}