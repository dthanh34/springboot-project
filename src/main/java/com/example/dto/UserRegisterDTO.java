package com.example.dto; // Đảm bảo đúng package của bạn

import lombok.Data;
import java.util.List;

@Data
public class UserRegisterDTO {
    // Step 1: Account
    private String name;
    private String email;
    private String password;

    // Step 2: Body Index
    private Boolean gender;
    private Integer age;
    private Double height;
    private Double weight;
    private Double activityLevel; // <--- Thuộc tính mới chúng ta vừa bàn

    // Step 3: Goals
    private Double desiredHeight;
    private Double desiredWeight;
    private String goalType;

    // Step 4: Health Restrictions (Lưu danh sách ID)
    private List<Integer> diseaseIds;
    private List<Integer> ingredientIds;
}