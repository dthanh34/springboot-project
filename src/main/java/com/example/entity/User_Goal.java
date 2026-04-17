package com.example.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "User_Goal")
public class User_Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="User_id")
    private User user;

    @Column(name="Goal_type")
    private String goalType;

    private float targetCalories;

}
