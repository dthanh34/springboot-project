package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String password;

    private String gender;
    private Integer age;
    private float weight;
    private float height;
    private float desiredWeight;
    private float desiredHeight;

    private String goalType; 
    private List<Integer> diseaseIds;
    private List<Integer> allergyIds;
}