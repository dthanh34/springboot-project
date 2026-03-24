package com.example.service;

import com.example.dto.UserSessionDTO;
import com.example.entity.*;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserGoalRepository userGoalRepository;
    private final UserDiseaseRepository userDiseaseRepository;
    private final UserAllergyRepository userAllergyRepository;
    private final HealthIndexRepository healthIndexRepository; 
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void completeRegistration(User user, String goalType, List<Integer> diseaseIds, List<Integer> ingredientIds) {
        // Ma hoa mat khau va thiet lap tai khoan ms tao = user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        User savedUser = userRepository.save(user);

        // luu muc tieu
        User_Goal goal = new User_Goal();
        goal.setUser(savedUser);
        goal.setGoal_type(goalType);
        goal.setTarget_calories(2000.0f); 
        userGoalRepository.save(goal);

        Health_Index healthIndex = new Health_Index();
        healthIndex.setUser(savedUser);
        
        float heightM = savedUser.getHeight() / 100;
        float bmi = savedUser.getWeight() / (heightM * heightM);
        healthIndex.setBmi(bmi);

        float bmr = (10 * savedUser.getWeight()) + (6.25f * savedUser.getHeight()) - (5 * savedUser.getAge());
        bmr = savedUser.getGender() ? (bmr + 5) : (bmr - 161);
        healthIndex.setBmr(bmr);

        healthIndex.setTdee(bmr * 1.2f);

        healthIndex.setCalculatedAt(LocalDateTime.now());

        healthIndexRepository.save(healthIndex);

        if (diseaseIds != null && !diseaseIds.isEmpty()) {
            for (Integer dId : diseaseIds) {
                User_disease ud = new User_disease();
                ud.setUser(savedUser);
                Disease disease = new Disease(); disease.setDisease_Id(dId);
                ud.setDisease(disease);
                userDiseaseRepository.save(ud);
            }
        }

        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            for (Integer iId : ingredientIds) {
                UserAllergy ua = new UserAllergy();
                ua.setUser(savedUser);
                Ingredient ing = new Ingredient(); ing.setIngredient_id(iId);
                ua.setIngredient(ing);
                userAllergyRepository.save(ua);
            }
        }
    }

    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public Optional<User> updateHealthMetrics(Long id, float weight, float height, Integer age) {
        return userRepository.findById(id).map(user -> {
            user.setWeight(weight);
            user.setHeight(height);
            user.setAge(age);
            return userRepository.save(user);
        });
    }

    public UserSessionDTO login(String name, String password) {
        return userRepository.findByName(name)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> {
                    UserSessionDTO dto = new UserSessionDTO();
                    dto.setId(user.getId());
                    dto.setName(user.getName());
                    dto.setRole(user.getRole());
                    dto.setAge(user.getAge());
                    dto.setWeight(user.getWeight());
                    dto.setHeight(user.getHeight());
                    dto.setGender(user.getGender());
                    return dto;
                })
                .orElse(null);
    }

    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}