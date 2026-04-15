package com.example.dto;
import lombok.Data;
@Data
public class UserSessionDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private Integer age;    
    private float weight;  
    private float height;
    private Boolean gender; 
    private Double activityLevel;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }

    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }

    public Boolean getGender() { return gender; }
    public void setGender(Boolean gender) { this.gender = gender; }
}