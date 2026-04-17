package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data

@Table(name = "User_Allergy")
public class UserAllergy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @ManyToOne
    @JoinColumn(name = "User_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "Ingredient_id")
    private Ingredient ingredient;

    private String reaction;
    private String note;

}