package com.example.service;

import com.example.entity.Food;
import com.example.entity.User;
import com.example.entity.Health_Index;
import com.example.repository.FoodRepository;
import com.example.repository.HealthIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Collections;

@Service
public class MealService {
    @Autowired private FoodRepository foodRepository;
    @Autowired private HealthIndexRepository healthRepo;

    public List<Food> suggestDailyMenu(Long userId) {
        // 1. Lấy TDEE mới nhất của User (Con số bạn vừa fix trong SQL)
        Health_Index health = healthRepo.findFirstByUserIdOrderByCalculatedAtDesc(userId);
        double tdee = health.getTdee();

        // 2. Tính toán mục tiêu Calo từng bữa
        double targetSáng = tdee * 0.25;
        double targetTrưa = tdee * 0.40;
        double targetTối = tdee * 0.35;

        // 3. Lấy danh sách ứng viên từ DB và chọn món sát Calo nhất
        Food sáng = pickBestFood(foodRepository.findSmartCandidate(userId, 1), targetSáng);
        Food trưa = pickBestFood(foodRepository.findSmartCandidate(userId, 2), targetTrưa);
        Food tối  = pickBestFood(foodRepository.findSmartCandidate(userId, 3), targetTối);

        return List.of(sáng, trưa, tối);
    }

    // Hàm tìm món ăn có Calo gần với mục tiêu nhất
    private Food pickBestFood(List<Food> foods, double target) {
        if (foods.isEmpty()) return null;
        return foods.stream()
                .min((f1, f2) -> Double.compare(
                        Math.abs(f1.getCalories() - target), 
                        Math.abs(f2.getCalories() - target)))
                .orElse(null);
    }
}