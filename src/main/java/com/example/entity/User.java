package com.example.entity;

import jakarta.persistence.*;

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

    

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getGender() { return gender; }
    public void setGender(Boolean gender) { this.gender = gender; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }

    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }

    public float getDesiredHeight() { return desiredHeight; }
    public void setDesiredHeight(float desiredHeight) { this.desiredHeight = desiredHeight; }

    public float getDesiredWeight() { return desiredWeight; }
    public void setDesiredWeight(float desiredWeight) { this.desiredWeight = desiredWeight; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}