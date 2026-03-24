package com.example.dto;

public class UserAllergyDTO {
    private Integer id;
    private Long userId;
    private Integer ingredientId;
    private String ingredientName;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getIngredientId() { return ingredientId; }
    public void setIngredientId(Integer ingredientId) { this.ingredientId = ingredientId; }

    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }
}