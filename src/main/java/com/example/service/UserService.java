package com.example.service;

import com.example.dto.LoginRequest;
import com.example.dto.ProgressDTO;
import com.example.dto.UserDTO;
import com.example.entity.*;
import com.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired private UserRepository userRepo;
    @Autowired private UserGoalRepository goalRepo;
    @Autowired private UserDiseaseRepository userDiseaseRepo;
    @Autowired private UserAllergyRepository userAllergyRepo;
    @Autowired private DiseaseRepository diseaseRepo;
    @Autowired private IngredientRepository ingredientRepo;
    @Autowired private WeightHeightHistoryRepository weightHistoryRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UserRepository userRepository;

    /**
     * Đăng ký người dùng mới (Dùng chung cho cả AuthRestController)
     */
    @Transactional
    public boolean registerNewUser(UserDTO dto) {
        try {
            if (existsByEmail(dto.getEmail())) return false;

            User user = new User();
            user.setName(dto.getFullName());
            user.setEmail(dto.getEmail());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setGender("Nam".equalsIgnoreCase(dto.getGender()));
            user.setAge(dto.getAge());
            user.setWeight(dto.getWeight());
            user.setHeight(dto.getHeight());
            user.setDesiredWeight(dto.getDesiredWeight());
            user.setDesiredHeight(dto.getDesiredHeight());
            user.setRole("USER");
            user.setIsActivate(1);
            
            User savedUser = userRepo.save(user);

            // Lưu quan hệ Bệnh lý & Dị ứng
            updateUserDiseases(savedUser, dto.getDiseaseIds());
            updateUserAllergies(savedUser, dto.getAllergyIds());

            // Khởi tạo mục tiêu dinh dưỡng
            UserGoal goal = new UserGoal();
            goal.setUser(savedUser);
            goal.setGoalType(dto.getGoalType() != null ? dto.getGoalType() : "Duy trì");
            goal.setTargetCalories((float) estimateTargetCalories(dto));
            goalRepo.save(goal);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public UserDTO getUserFullProfile(Long userId){
        User user = userRepo.findById(userId)
            .orElseThrow(()-> new RuntimeException("Khong tim thay id"));
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setFullName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setGender(user.getGender() ? "Nam" : "Nữ");
            dto.setAge(user.getAge());
            dto.setWeight(user.getWeight());
            dto.setHeight(user.getHeight());
            dto.setDesiredWeight(user.getDesiredWeight());
            dto.setDesiredHeight(user.getDesiredHeight());
            dto.setDiseaseIds(userDiseaseRepo.findByUserId(userId).stream()
                    .map(ud -> (Integer) ud.getDisease().getDiseaseId()) // Ép kiểu Integer rõ ràng
                    .collect(Collectors.toList()));
            dto.setAllergyIds(userAllergyRepo.findByUserId(userId).stream()
                .map(ua -> (Integer) ua.getIngredient().getIngredientId()).collect(Collectors.toList()));

            return dto;

    }

    /**
     * Cập nhật hồ sơ và lưu lịch sử cân nặng
     */
    @Transactional
    public void updateFullProfile(Long userId, UserDTO dto) {
        User user = userRepo.findById(userId).orElseThrow();
        user.setName(dto.getFullName());
        user.setGender("Nam".equalsIgnoreCase(dto.getGender()));
        user.setAge(dto.getAge());
        user.setWeight(dto.getWeight());
        user.setHeight(dto.getHeight());
        user.setDesiredWeight(dto.getDesiredWeight());
        user.setDesiredHeight(dto.getDesiredHeight());
        userRepo.save(user);

        updateUserDiseases(user, dto.getDiseaseIds());
        updateUserAllergies(user, dto.getAllergyIds());
        
        // Lưu lịch sử biến động (Dùng trực tiếp kiểu Long cho userId)
        updateWeightHeightHistory(userId, dto.getWeight(), dto.getHeight());
    }

    /**
     * Xử lý đăng nhập
     */
    public Map<String, Object> authenticate(LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepo.findByEmail(loginRequest.getEmail()).orElse(null);

        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            if (user.getIsActivate() != null && user.getIsActivate() == 0) {
                response.put("status", "INACTIVE");
                return response;
            }

            response.put("status", "SUCCESS");
            response.put("redirect", "ADMIN".equalsIgnoreCase(user.getRole()) ? "/admin/dashboard" : "/home");
            return response;
        }
        response.put("status", "ERROR");
        return response;
    }

    /**
     * Lấy dữ liệu biểu đồ tiến trình
     */
    public ProgressDTO getProgressSummary(Long userId) {

    User user = userRepo.findById(userId).orElseThrow();

    ProgressDTO dto = new ProgressDTO();

    float weight = user.getWeight();
    float height = user.getHeight();
    float desiredWeight = user.getDesiredWeight();
    float desiredHeight = user.getDesiredHeight();

    // BMI
    float hMeter = height / 100f;
    float bmi = weight / (hMeter * hMeter);

    dto.setBmi(bmi);

    // Calories demo tạm
    dto.setTodayCalories(1200f);

    dto.setCurrentWeight(weight);
    dto.setCurrentHeight(height);

    dto.setTargetWeight(desiredWeight);
    dto.setTargetHeight(desiredHeight);

    dto.setTotalDaysFollowed(30);

    dto.setGoalLabel("Đang cải thiện");

    // Progress weight
    float weightPercent = (weight / desiredWeight) * 100f;
    dto.setWeightProgressPercent(Math.min(weightPercent, 100));

    // Progress height
    float heightPercent = (height / desiredHeight) * 100f;
    dto.setHeightProgressPercent(Math.min(heightPercent, 100));

    return dto;
}
    /**
     * Đổi mật khẩu
     */
    @Transactional
    public Map<String, Object> changePassword(Long userId, String oldPassword, String newPassword) {
        Map<String, Object> res = new HashMap<>();
        User user = userRepo.findById(userId).orElse(null);

        if (user != null && passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(user);
            res.put("success", true);
            res.put("message", "Đổi mật khẩu thành công!");
        } else {
            res.put("success", false);
            res.put("message", "Mật khẩu cũ không chính xác!");
        }
        return res;
    }

    /**
     * Lấy thông tin cài đặt (Settings)
     */
    public Map<String, Object> getSettingsProfile(Long userId) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepo.findById(userId).orElse(null);
        UserGoal goal = goalRepo.findTopByUserIdOrderByIdDesc(userId).orElse(null);

        Map<String, Object> userData = new HashMap<>();
        Map<String, Object> goalData = new HashMap<>();

        if (user != null) {
            userData.put("fullName", user.getName());
            userData.put("email", user.getEmail());
            goalData.put("goalType", goal != null ? goal.getGoalType() : "Duy trì");
            goalData.put("targetCalories", goal != null ? goal.getTargetCalories() : 2000);
        }

        response.put("user", userData);
        response.put("goal", goalData);
        return response;
    }

    @Transactional
    public void updateWeightHeight(Long userId, Float weight, Float height) {
        User user = userRepo.findById(userId).orElseThrow();
        user.setWeight(weight);
        user.setHeight(height);
        userRepo.save(user);
        updateWeightHeightHistory(userId, weight, height);
    }

    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    // --- PRIVATE HELPER METHODS ---

    private void updateWeightHeightHistory(Long userId, Float weight, Float height) {
        WeightHeightHistory history = new WeightHeightHistory();
        history.setUserId(userId);
        history.setWeight(weight);
        history.setHeight(height);
        weightHistoryRepo.save(history);
    }

    private void updateUserDiseases(User user, List<Integer> diseaseIds) {
        userDiseaseRepo.deleteByUserId(user.getId());
        if (diseaseIds != null) {
            diseaseIds.forEach(id -> {
                UserDisease ud = new UserDisease();
                ud.setUser(user);
                diseaseRepo.findById(id).ifPresent(ud::setDisease);
                userDiseaseRepo.save(ud);
            });
        }
    }

    private void updateUserAllergies(User user, List<Integer> allergyIds) {
        userAllergyRepo.deleteByUserId(user.getId());
        if (allergyIds != null) {
            allergyIds.forEach(id -> {
                UserAllergy ua = new UserAllergy();
                ua.setUser(user);
                ingredientRepo.findById(id).ifPresent(ua::setIngredient);
                userAllergyRepo.save(ua);
            });
        }
    }

    private double estimateTargetCalories(UserDTO dto) {
        double bmr = "Nam".equalsIgnoreCase(dto.getGender()) 
            ? (10 * dto.getWeight() + 6.25 * dto.getHeight() - 5 * dto.getAge() + 5)
            : (10 * dto.getWeight() + 6.25 * dto.getHeight() - 5 * dto.getAge() - 161);
        double tdee = bmr * 1.4;
        if ("Giảm cân".equalsIgnoreCase(dto.getGoalType())) return Math.max(1200, tdee - 300);
        if ("Tăng cân".equalsIgnoreCase(dto.getGoalType())) return tdee + 300;
        return tdee;
    }
    /**
     * Cập nhật thông tin cá nhân cơ bản (Họ tên và Email)
     */
    @Transactional
    public Map<String, Object> updatePersonalSettings(Long userId, String fullName, String email) {
        Map<String, Object> res = new HashMap<>();
        User user = userRepo.findById(userId).orElse(null);
        
        if (user != null) {
            user.setName(fullName);
            user.setEmail(email);
            userRepo.save(user);
            res.put("success", true);
            res.put("message", "Cập nhật thông tin thành công!");
        } else {
            res.put("success", false);
            res.put("message", "Không tìm thấy người dùng!");
        }
        return res;
    }

    /**
     * Cập nhật mục tiêu dinh dưỡng vào bảng UserGoal thật
     */
    @Transactional
    public Map<String, Object> updateNutritionGoal(Long userId, String goalType, Object targetCalories) {
        Map<String, Object> res = new HashMap<>();
        // Lấy mục tiêu mới nhất của Thành để cập nhật
        UserGoal goal = goalRepo.findTopByUserIdOrderByIdDesc(userId).orElse(new UserGoal());
        
        if (goal.getUser() == null) {
            User user = userRepo.findById(userId).orElse(null);
            goal.setUser(user);
        }
        
        goal.setGoalType(goalType);
        // Chuyển đổi Object sang Float để khớp với Entity UserGoal của Thành
        goal.setTargetCalories(Float.parseFloat(targetCalories.toString()));
        
        goalRepo.save(goal);

        res.put("success", true);
        res.put("message", "Cập nhật mục tiêu thành công!");
        return res;
    }

    public UserDTO getCurrentUser(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getName());
        dto.setEmail(user.getEmail());

        return dto;
    }
}