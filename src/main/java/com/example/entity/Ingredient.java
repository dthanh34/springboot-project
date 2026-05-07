package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "ingredient")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Ingredient_id")
    private Integer ingredientId;

    @Column(name="Ingredient_name")
    private String ingredientName;

    private float calories;
    private String category;
    @Column(name="Protein")
    private float protein;
    private float fat;
    private float carbohydrate;

}