package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_id")
    private Long id;

    private String name;
    private String password;
    private String email;

    private Boolean gender; 
    private Integer age;    

    private float weight;   
    private float height;

    @Column(name = "desired_height")
    private float desiredHeight;

    @Column(name = "desired_weight")
    private float desiredWeight;

    @Column(name = "Role")
    private String role;

}