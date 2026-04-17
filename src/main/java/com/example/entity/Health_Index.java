package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "Health_Index")
@Data
public class Health_Index {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private float bmi;
    private float bmr;
    private float tdee;
    
    @Column(name = "activity_level")
    private Double activityLevel;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt = LocalDateTime.now();

}