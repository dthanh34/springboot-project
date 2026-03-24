package com.example.controller;

import com.example.entity.Health_Index;
import com.example.entity.User;
import com.example.repository.HealthIndexRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@CrossOrigin 
public class HealthIndexController {

    @Autowired
    private HealthIndexRepository healthIndexRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/update")
    public ResponseEntity<?> updateHealthIndex(@RequestBody Map<String, Object> data) {
        try {
            // lay id 
            Long userId = Long.valueOf(data.get("userId").toString());

            // tim user
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("Lỗi: Không tìm thấy người dùng!");
            }

            Health_Index healthIndex = new Health_Index();
            healthIndex.setUser(user);
            
            healthIndex.setBmi(Float.parseFloat(data.get("bmi").toString()));
            healthIndex.setBmr(Float.parseFloat(data.get("bmr").toString()));
            healthIndex.setTdee(Float.parseFloat(data.get("tdee").toString()));
            
            healthIndex.setCalculatedAt(LocalDateTime.now());

            // luu Database
            healthIndexRepository.save(healthIndex);

            return ResponseEntity.ok("Đã cập nhật chỉ số sức khỏe vào hệ thống thành công!");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @GetMapping("/latest/{userId}")
    public ResponseEntity<?> getLatestIndex(@PathVariable Integer userId) {
        Health_Index latest = healthIndexRepository.findFirstByUserIdOrderByCalculatedAtDesc(userId);
        if (latest == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(latest);
    }
}