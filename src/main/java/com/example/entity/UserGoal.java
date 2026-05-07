package com.example.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_goal")
public class UserGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name="User_id")
    private User user;

    @Column(name="Goal_type")
    private String goalType;

    @Column(name="target_calories")
    private float targetCalories;

}
