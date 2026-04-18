package com.example.entity;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Entity
@Table(name="Daily_Menu")
public class DailyMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="menu_id")
    private Integer menuId;

    @Column(name="User_id")
    private Long userId;

    @Column(name = "plan_id")
    private Integer planId;

    @Column(name = "Menu_date")
    private LocalDate menuDate;

    @Column(name = "total_calories")
    private Double totalCalories;

    @Column(name = "Status")
    private String status;

}
