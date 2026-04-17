package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "User_disease")
@Data 
public class User_disease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @ManyToOne
    @JoinColumn(name = "User_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "Disease_id")
    private Disease disease;
     
    private String level;
    private String discoveryDate;
    private String note;

}