package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "user_login")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserLoginId.class) 
public class UserLogin implements Serializable {

    @Id
    @Column(name = "id")
    private int userId; 

    @Id
    @Column(name = "login_date")
    private LocalDate loginDate;
}

