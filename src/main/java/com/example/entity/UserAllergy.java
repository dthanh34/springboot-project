package com.example.entity;

import jakarta.persistence.*;

@Entity
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

    public Integer getId() { return Id; }
    public void setId(Integer Id) { this.Id = Id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
}