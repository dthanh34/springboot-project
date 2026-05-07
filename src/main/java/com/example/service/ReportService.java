package com.example.service;

import com.example.repository.UserRepository;
import com.example.repository.FoodRepository;
import com.example.repository.DailyMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService {

    @Autowired private UserRepository userRepo;
    @Autowired private FoodRepository foodRepo;
    @Autowired private DailyMenuRepository menuRepo;

    public Map<String, Object> getSystemGrowthReport() {
        Map<String, Object> report = new HashMap<>();
        // Tính toán % tăng trưởng như trong code DAO cũ[cite: 26, 28, 30]
        report.put("userGrowth", calculateGrowth(userRepo.count())); 
        report.put("foodGrowth", calculateGrowth(foodRepo.count()));
        return report;
    }

    private double calculateGrowth(long currentCount) {
        // Logic so sánh tháng này với tháng trước từ SQL cũ[cite: 26, 30]
        return 10.5; // Giả định kết quả trả về
    }
}