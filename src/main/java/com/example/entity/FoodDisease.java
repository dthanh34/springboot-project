package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "food_disease")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodDisease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "Food_id")
    private Food food;

    @ManyToOne
    @JoinColumn(name = "Disease_id")
    private Disease disease;

    @Column(name = "Rating")
    private Integer rating; // Điểm đánh giá mức độ phù hợp 
}