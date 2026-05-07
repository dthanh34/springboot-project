package com.example.service;

import com.example.repository.*;
import com.example.entity.*; // Đảm bảo import đúng Entity User
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.time.LocalDate;

@Service
public class AdminService {

    @Autowired private UserRepository userRepo;
    @Autowired private FoodRepository foodRepo;
    @Autowired private DailyMenuRepository dailyMenuRepo;
    @Autowired private UserLoginRepository loginRepo; 

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepo.count());
        stats.put("totalFoods", foodRepo.count());
        stats.put("totalMenus", dailyMenuRepo.count());
        
        stats.put("todayActivities", loginRepo.countByLoginDate(LocalDate.now()));
        return stats;
    }

    public void toggleUserStatus(Long userId, boolean activate) {
        userRepo.findById(userId).ifPresent(user -> {
            user.setIsActivate(activate ? 1 : 0); 
            userRepo.save(user);
        });
    }

    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userRepo.count());
        data.put("totalFoods", foodRepo.count());
        
        data.put("chartLabels", Arrays.asList("Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "CN"));
        data.put("chartData", Arrays.asList(10, 20, 15, 30, 25, 40, 35));
        
        return data;
    }

    public Map<String, Object> getUsersPaged(int page, String keyword, String role) {
        Map<String, Object> result = new HashMap<>();
        result.put("users", userRepo.findAll()); 
        result.put("totalCount", userRepo.count());
        return result;
    }
}