package com.example.dto;

public class IngredientDTO {
    private Integer ingredient_id;
    private String ingredient_name;
    private float calories;
    private String category;
    private float protein;
    private float fat;
    private float carbohydrate;

    public Integer getIngredient_id() { return ingredient_id; }
    public void setIngredient_id(Integer ingredient_id) { this.ingredient_id = ingredient_id; }

    public String getIngredient_name() { return ingredient_name; }
    public void setIngredient_name(String ingredient_name) { this.ingredient_name = ingredient_name; }

    public float getCalories() { return calories; }
    public void setCalories(float calories) { this.calories = calories; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public float getProtein() { return protein; }
    public void setProtein(float protein) { this.protein = protein; }

    public float getFat() { return fat; }
    public void setFat(float fat) { this.fat = fat; }

    public float getCarbohydrate() { return carbohydrate; }
    public void setCarbohydrate(float carbohydrate) { this.carbohydrate = carbohydrate; }
}