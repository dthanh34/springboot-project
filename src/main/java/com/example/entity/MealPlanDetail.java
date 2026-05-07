package com.example.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "meal_plan_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "layout_id")
    private Integer layoutId;

    @Column(name = "Setting_id")
    private Integer settingId;

    @Column(name = "Meal_type_id")
    private Integer mealTypeId;

    @Column(name = "portion_size")
    private Double portionSize; 

    @Column(name = "cook_level")
    private Integer cookLevel;

    @Column(name = "need_cooking")
    private Boolean needCooking; 

    @Column(name = "display_order")
    private Integer displayOrder;

}