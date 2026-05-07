package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_disease")
@Data 
public class UserDisease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "User_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "Disease_id")
    private Disease disease;

}