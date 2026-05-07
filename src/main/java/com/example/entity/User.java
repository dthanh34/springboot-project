package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_id")
    private long id;

    // Trong SQL của bạn là cột [name], bạn đặt biến là fullName nên cần @Column
    @Column(name = "name")
    private String name;

    private String email;
    private String password;
    
    // SQL là BIT, Java cũ bạn dùng String. 
    // Nếu SQL lưu 0/1 thì dùng String hoặc Boolean đều được, ở đây tôi để String theo code cũ của bạn.
    private Boolean gender;
    
    private int age;
    private float weight;
    private float height;
    
     @Column(name = "desired_weight")
    private float desiredWeight;
    @Column(name = "desired_height")
    private float desiredHeight;
    
    @Column(name = "Role")
    private String role;

    // SQL là is_activate, Java cũ cũng là is_activate
    @Column(name = "is_activate")
    private Integer isActivate; 

    @Column(name = "create_at")
    private String createdAt;

    // Constructor tùy chỉnh theo code cũ của bạn
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}