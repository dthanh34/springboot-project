package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "food_ingredient")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "Food_id")
    private Food food;

    @ManyToOne
    @JoinColumn(name = "Ingredient_id")
    private Ingredient ingredient;

    @Column(name = "Quantity")
    private Double quantity; // Định lượng nguyên liệu

    @Column(name = "Unit")
    private String unit; // Đơn vị tính (g, ml, quả...)
}