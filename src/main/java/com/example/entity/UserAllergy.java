package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data

@Table(name = "user_allergy")
public class UserAllergy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "User_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "Ingredient_id")
    private Ingredient ingredient;

}