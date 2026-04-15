package com.example.controller;

import com.example.dto.LoginDTO;
import com.example.dto.UserSessionDTO;
import com.example.dto.UserRegisterDTO; // Import DTO mới
import com.example.entity.User;
import com.example.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    // 1. API Đăng nhập (Giữ nguyên của bạn)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request, HttpServletRequest servletRequest) {
        UserSessionDTO user = userService.login(request.getName(), request.getPassword());

        if (user == null) {
            return ResponseEntity.status(401).body("Sai tài khoản hoặc mật khẩu");
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user.getName(), 
                null, 
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        HttpSession session = servletRequest.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return ResponseEntity.ok(user);
    }

    // 2. API Đăng ký hoàn tất (Đã tối ưu dùng DTO)
    @PostMapping("/register-complete")
    public ResponseEntity<?> completeRegistration(@RequestBody UserRegisterDTO dto) {
        try {
            // Khởi tạo User và map dữ liệu từ DTO
            User user = new User();
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPassword(dto.getPassword());
            user.setAge(dto.getAge());
            user.setGender(dto.getGender());
            
            // Map các chỉ số cơ thể
            user.setWeight(dto.getWeight().floatValue());
            user.setHeight(dto.getHeight().floatValue());
            user.setDesiredHeight(dto.getDesiredHeight().floatValue());
            user.setDesiredWeight(dto.getDesiredWeight().floatValue());

            // Gọi UserService thực hiện lưu liên hoàn (Đã có thêm activityLevel)
            userService.completeRegistration(
                user, 
                dto.getGoalType(), 
                dto.getDiseaseIds(), 
                dto.getIngredientIds(), 
                dto.getActivityLevel().floatValue()
            );

            return ResponseEntity.ok("Đăng ký thành công và đã lưu vào hệ thống!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi lưu dữ liệu: " + e.getMessage());
        }
    }
}