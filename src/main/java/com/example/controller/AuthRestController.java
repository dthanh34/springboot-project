package com.example.controller;

import com.example.dto.LoginRequest;
import com.example.dto.UserDTO;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import com.example.service.DiseaseService;
import com.example.service.IngredientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepo;
    @Autowired private DiseaseService diseaseService;
    @Autowired private IngredientService ingredientService;
    
    
    @Autowired private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
             Map<String, Object> authResult = userService.authenticate(loginRequest);
            String status = (String) authResult.get("status");

            if ("INACTIVE".equals(status)) {
                Map<String, Object> errorRes = new HashMap<>();
                errorRes.put("status", "INACTIVE");
                errorRes.put("message", "Tài khoản đã bị vô hiệu hóa. Vui lòng liên hệ quản trị viên.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorRes);
            }

            if (!"SUCCESS".equals(status)) {
                Map<String, Object> errorRes = new HashMap<>();
                errorRes.put("status", "ERROR");
                errorRes.put("message", "Email hoặc mật khẩu không chính xác");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorRes);
            }

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            User user = userRepo.findByEmail(loginRequest.getEmail())
                        .orElseThrow(() -> new RuntimeException("User not found"));
            
            
            session.setAttribute("userId", user.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            
            
            response.put("redirect", "ADMIN".equalsIgnoreCase(user.getRole()) ? "/admin/dashboard" : "/home");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorRes = new HashMap<>();
            errorRes.put("status", "ERROR");
            errorRes.put("message", "Email hoặc mật khẩu không chính xác");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorRes);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        boolean success = userService.registerNewUser(userDTO);
        return success ? ResponseEntity.ok("SUCCESS") : ResponseEntity.ok("ERROR");
    }

    @GetMapping("/check-email")
    public Map<String, Boolean> checkEmail(@RequestParam String email) {
        Map<String, Boolean> res = new HashMap<>();
        res.put("exists", userService.existsByEmail(email));
        return res;
    }

    @GetMapping("/register-data")
    public Map<String, Object> getRegisterData() {
        Map<String, Object> res = new HashMap<>();
        res.put("listIngredient", ingredientService.getAllIngredients());
        res.put("listDisease", diseaseService.getAllDiseases());
        return res;
    }
}