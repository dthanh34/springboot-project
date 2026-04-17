package com.example.dto;
import lombok.Data;
import java.util.List;
@Data
// @AllArgsConstructor
// @NoArgsConstructor
public class UserProfileDTO {
    private Long userId;
    private String fullName;
    private int age;
    private Boolean gender;
    private float weight;
    private float height;
    private float desiredHeight;
    private float desiredWeight;

    private String goalType;
     
    private List<UserDiseaseDTO> disease;

    private List<UserAllergyDTO> allergy;
}
