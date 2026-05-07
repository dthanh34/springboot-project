package com.example.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "meal_type")
@Data
public class MealType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_type_id")
    private Integer mealTypeId;

    @Column(name="meal_name")
    private String mealName;

    @Column(name="target_calories")
    private Float targetCalories;

}
