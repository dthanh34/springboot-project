package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; 

    @ManyToOne
    @JoinColumn(name = "Food_id")
    private Food food; 

    @Column(name = "eaten_at")
    private LocalDateTime eatenAt; 
}