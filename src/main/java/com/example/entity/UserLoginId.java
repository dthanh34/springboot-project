package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginId implements Serializable {
    private int userId; 
    private LocalDate loginDate; 
}