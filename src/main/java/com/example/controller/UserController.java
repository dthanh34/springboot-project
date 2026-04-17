package com.example.controller;

import com.example.dto.UserProfileDTO;
import com.example.service.UserService;
import com.example.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile-data")
    public ResponseEntity<?> getProfileData(org.springframework.security.core.Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("Chưa đăng nhập!");
        }

        // Lấy name của người đang đăng nhập
        String name = auth.getName();
        User user = userService.findByName(name);

        if (user == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(userService.getUserProfile(user.getId()));
    }
}