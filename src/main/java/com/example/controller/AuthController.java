package com.example.controller;

import com.example.dto.LoginDTO;
import com.example.dto.UserSessionDTO;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    // 1. API Đăng nhập (Đã thêm cơ chế tạo Session cho Spring Security)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request, HttpServletRequest servletRequest) {
        // Gọi UserService kiểm tra name và password (đã mã hóa)
        UserSessionDTO user = userService.login(request.getName(), request.getPassword());

        if (user == null) {
            return ResponseEntity.status(401).body("Sai tài khoản hoặc mật khẩu");
        }

        // Tạo đối tượng Authentication với Role lấy từ database (USER/ADMIN)
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

    @PostMapping("/register-complete")
    public ResponseEntity<?> completeRegistration(@RequestBody Map<String, Object> data) {
        try {
            // Khởi tạo User và gán dữ liệu từ JSON gửi lên
            User user = new User();
            user.setName((String) data.get("name"));
            user.setEmail((String) data.get("email"));
            user.setPassword((String) data.get("password"));
            
            // Ép kiểu Integer cho tuổi
            user.setAge(Integer.parseInt(data.get("age").toString()));
            
            // Xử lý kiểu số thực (float) tránh lỗi ép kiểu từ Double của Jackson
            user.setWeight(Float.parseFloat(data.get("weight").toString()));
            user.setHeight(Float.parseFloat(data.get("height").toString()));
            user.setGender((Boolean) data.get("gender"));
            
            // Chỉ số mục tiêu
            user.setDesiredHeight(Float.parseFloat(data.get("desiredHeight").toString()));
            user.setDesiredWeight(Float.parseFloat(data.get("desiredWeight").toString()));

            // Trích xuất thông tin mục tiêu và danh sách ID bệnh lý/dị ứng
            String goalType = (String) data.get("goalType");
            
            // Ép kiểu danh sách ID
            List<Integer> diseaseIds = (List<Integer>) data.get("diseaseIds");
            List<Integer> ingredientIds = (List<Integer>) data.get("ingredientIds");

            // Gọi UserService thực hiện lưu liên hoàn vào nhiều bảng
            userService.completeRegistration(user, goalType, diseaseIds, ingredientIds);

            return ResponseEntity.ok("Đăng ký thành công và đã lưu vào hệ thống!");
        } catch (Exception e) {
            e.printStackTrace(); // In ra console để debug lỗi ép kiểu nếu có
            return ResponseEntity.badRequest().body("Lỗi khi lưu dữ liệu: " + e.getMessage());
        }
    }
}