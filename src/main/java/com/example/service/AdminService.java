package com.example.service;

import com.example.repository.*;
import com.example.entity.*; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.sql.Date;
import java.util.*;

@Service
public class AdminService {

    @Autowired private UserRepository userRepo;
    @Autowired private FoodRepository foodRepo;
    @Autowired private DailyMenuRepository dailyMenuRepo;
     @Autowired private UserLoginRepository loginRepo;
    @Autowired private UserFavoriteRepository userFavoriteRepo;
    @Autowired private UserGoalRepository userGoalRepo;

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
        long totalUsers = userRepo.count();
        long totalFoods = foodRepo.count();
        long totalMenus = dailyMenuRepo.count();

        data.put("name", "Admin");
        data.put("totalUsers", totalUsers);
        data.put("totalFoods", totalFoods);
        data.put("totalMenus", totalMenus);
        data.put("todayActivities", loginRepo.countByLoginDate(LocalDate.now()));

        data.put("userGrowth", calcGrowthPercent(userRepo.countUsersThisMonth(), userRepo.countUsersLastMonth()));
        data.put("foodGrowth", calcGrowthPercent(foodRepo.countFoodsThisMonth(), foodRepo.countFoodsLastMonth()));
        data.put("menuGrowth", calcGrowthPercent(dailyMenuRepo.countMenusThisMonth(), dailyMenuRepo.countMenusLastMonth()));

        List<Integer> userChartData = new ArrayList<>(Collections.nCopies(12, 0));
        for (Object[] row : userRepo.countNewUsersByMonthInCurrentYear()) {
            int month = ((Number) row[0]).intValue();
            int count = ((Number) row[1]).intValue();
            if (month >= 1 && month <= 12) {
                userChartData.set(month - 1, count);
            }
        }
        data.put("userChartData", userChartData);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        Map<String, Long> loginStats = new LinkedHashMap<>();
        for (int i = 9; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            loginStats.put(day.format(formatter), 0L);
        }
         for (Object[] row : loginRepo.countDistinctUsersByLoginDateBetweenLast10Days()) {
            String dayLabel;
            Object rawDate = row[0];
            if (rawDate instanceof Date sqlDate) {
                dayLabel = sqlDate.toLocalDate().format(formatter);
            } else {
                try {
                    dayLabel = LocalDate.parse(String.valueOf(rawDate)).format(formatter);
                } catch (DateTimeParseException e) {
                    dayLabel = String.valueOf(rawDate);
                }
            }
            long loginCount = ((Number) row[1]).longValue();
            if (loginStats.containsKey(dayLabel)) {
                loginStats.put(dayLabel, loginCount);
            }
        }
        data.put("chartLabels", new ArrayList<>(loginStats.keySet()));
        data.put("chartData", new ArrayList<>(loginStats.values()));

        LinkedHashMap<String, Integer> top3 = new LinkedHashMap<>();
        for (Object[] row : userFavoriteRepo.countTopFoods()) {
            if (row[0] == null || row[1] == null) {
                continue;
            }
            top3.put(String.valueOf(row[0]), ((Number) row[1]).intValue());
        }
        data.put("topFoods", top3);

        Map<String, Double> popularGoals = new LinkedHashMap<>();
        List<Object[]> goals = userGoalRepo.countGoalsByType();
        double totalGoals = goals.stream().mapToDouble(r -> ((Number) r[1]).doubleValue()).sum();
        if (totalGoals > 0) {
            for (Object[] goal : goals) {
                if (goal[0] == null) {
                    continue;
                }
                String goalType = String.valueOf(goal[0]);
                double count = ((Number) goal[1]).doubleValue();
                popularGoals.put(goalType, (count * 100.0) / totalGoals);
            }
        }
        data.put("popularGoals", popularGoals);
        
        return data;
    }

    public Map<String, Object> getUsersPaged(int page, String keyword, String role) {
        Map<String, Object> result = new HashMap<>();
        result.put("users", userRepo.findAll()); 
        result.put("totalCount", userRepo.count());
        return result;
    }
 private double calcGrowthPercent(long thisMonth, long lastMonth) {
        if (lastMonth <= 0) {
            return thisMonth > 0 ? 100.0 : 0.0;
        }
        return ((double) (thisMonth - lastMonth) / lastMonth) * 100.0;
    }
}