package com.example.dto;

import lombok.Data;
import java.util.Map;
import java.util.List;

@Data
public class DashboardDTO {
    private int totalUsers;
    private int totalFoods;
    private int totalMenus;
    private int todayActivities;
    
    private double userGrowth;
    private double foodGrowth;
    private double menuGrowth;

    private List<Integer> userMonthlyStats; 
    private Map<String, Integer> loginStats; 
    private Map<String, Integer> topFoods; 
}