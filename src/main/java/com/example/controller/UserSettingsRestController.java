package com.example.controller;

import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/user/settings")
public class UserSettingsRestController {

    @Autowired private UserService userService;

    @GetMapping("/data")
    public ResponseEntity<?> getSettingsData(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return ResponseEntity.ok(userService.getSettingsProfile(userId));
    }

    @PostMapping("/personal")
    public Map<String, Object> updatePersonal(@RequestBody Map<String, String> data, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return userService.updatePersonalSettings(userId, data.get("fullName"), data.get("email"));
    }

    @PostMapping("/goal")
    public Map<String, Object> updateGoal(@RequestBody Map<String, Object> data, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return userService.updateNutritionGoal(userId, (String) data.get("goalType"), data.get("targetCalories"));
    }

    @PostMapping("/password")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> data, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return userService.changePassword(userId, data.get("oldPassword"), data.get("newPassword"));
    }
}