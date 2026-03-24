package com.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Ingredient")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Ingredient_id")
    private Integer Ingredient_id;

    private String Ingredient_name;
    private float calories;
    private String category;

    @Column(name = "Protein")
    private float Protein;

    private float fat;
    private float carbohydrate;

    public Integer getIngredient_id() { return Ingredient_id; }
    public void setIngredient_id(Integer Ingredient_id) { this.Ingredient_id = Ingredient_id; }

    public String getIngredient_name() { return Ingredient_name; }
    public void setIngredient_name(String Ingredient_name) { this.Ingredient_name = Ingredient_name; }

    public float getCalories() { return calories; }
    public void setCalories(float calories) { this.calories = calories; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public float getProtein() { return Protein; }
    public void setProtein(float Protein) { this.Protein = Protein; }

    public float getFat() { return fat; }
    public void setFat(float fat) { this.fat = fat; }

    public float getCarbohydrate() { return carbohydrate; }
    public void setCarbohydrate(float carbohydrate) { this.carbohydrate = carbohydrate; }
}