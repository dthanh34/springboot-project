package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "ingredient_disease")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDisease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "Ingredient_id")
    private Ingredient ingredient;

    @ManyToOne
    @JoinColumn(name = "Disease_id")
    private Disease disease;

    @Column(name = "Is_Ky")
    private Boolean isKy;

    @Column(name = "Note")
    private String note;
}