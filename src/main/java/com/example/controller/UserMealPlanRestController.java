package com.example.controller;

import com.example.dto.UserDTO;
import com.example.entity.User;
import com.example.service.MealPlanService;
import com.example.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserMealPlanRestController {

    @Autowired private MealPlanService mealPlanService;
    
    private final UserService userService;

    @GetMapping("/home-summary")
    public ResponseEntity<?> getHomeSummary(Authentication authentication) {
        UserDTO currentUser =
            userService.getCurrentUser(authentication);
        return ResponseEntity.ok(
            mealPlanService.getHomeDashboardData(currentUser.getId())
        );
    }

    @GetMapping("/meal-plan")
    public ResponseEntity<?> getMealPlan(@RequestParam String date, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        return ResponseEntity.ok(mealPlanService.getDailyMealPlan(user.getId(), date));
    }

    @PostMapping("/meal-plan/update")
    public ResponseEntity<?> updateMeal(@RequestBody Map<String, Object> payload, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        mealPlanService.updateUserMeal(user.getId(), payload);
        return ResponseEntity.ok().build();
    }
}