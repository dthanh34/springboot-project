package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "adjusted_recipe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdjustedRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Food_id")
    private Integer foodId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "recipe", columnDefinition = "NVARCHAR(MAX)")
    private String recipe;

    @Column(name = "calories")
    private Float calories;

    @Column(name = "fat")
    private Float fat;

    @Column(name = "Protein") 
    private Float protein;

    @Column(name = "carbohydrate")
    private Float carbohydrate;
}