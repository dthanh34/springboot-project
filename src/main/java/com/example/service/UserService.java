package com.example.service;

import com.example.dto.UserAllergyDTO;
import com.example.dto.UserDiseaseDTO;
import com.example.dto.UserProfileDTO;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserGoalRepository userGoalRepository;
    private final UserDiseaseRepository userDiseaseRepository;
    private final UserAllergyRepository userAllergyRepository;
    private final HealthIndexRepository healthIndexRepository;
    private final DiseaseRepository diseaseRepository;
    private final IngredientRepository ingredientRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void completeRegistration(User user, String goalType, List<Integer> diseaseIds, List<Integer> ingredientIds,Float activityLevel) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        User savedUser = userRepository.save(user);
        User_Goal goal = new User_Goal();
        goal.setUser(savedUser);
        goal.setGoalType(goalType);
        goal.setTargetCalories(2000.0f);
        userGoalRepository.save(goal);

        Health_Index healthIndex = new Health_Index();
        healthIndex.setUser(savedUser);

        float heightM = savedUser.getHeight() / 100;
        float bmi = savedUser.getWeight() / (heightM * heightM);
        healthIndex.setBmi(bmi);

        float bmr = (10 * savedUser.getWeight()) + (6.25f * savedUser.getHeight()) - (5 * savedUser.getAge());
        bmr = savedUser.getGender() ? (bmr + 5) : (bmr - 161);
        healthIndex.setBmr(bmr);

        float multiplier = (activityLevel != null) ? activityLevel : 1.2f; 
        healthIndex.setTdee(bmr * multiplier);
        healthIndex.setActivityLevel(Double.valueOf(multiplier));
        healthIndex.setCalculatedAt(LocalDateTime.now());

        healthIndexRepository.save(healthIndex);

        if (diseaseIds != null && !diseaseIds.isEmpty()) {

            List<Disease> diseases = diseaseRepository.findAllById(diseaseIds);

            for (Disease disease : diseases) {
                User_disease ud = new User_disease();
                ud.setUser(savedUser);
                ud.setDisease(disease);
                userDiseaseRepository.save(ud);
            }
        }

        if (ingredientIds != null && !ingredientIds.isEmpty()) {

            List<Ingredient> ingredients = ingredientRepository.findAllById(ingredientIds);

            for (Ingredient ing : ingredients) {
                UserAllergy ua = new UserAllergy();
                ua.setUser(savedUser);
                ua.setIngredient(ing);
                userAllergyRepository.save(ua);
            }
        }
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
                    Health_Index health = healthIndexRepository.findFirstByUserIdOrderByCalculatedAtDesc(user.getId());
                    if(health != null){
                        dto.setActivityLevel(health.getActivityLevel());
                    } else {
                        dto.setActivityLevel(1.2);
                    }
                    return dto;
                })
                .orElse(null);
    }
    public User findByName(String name) {
        return userRepository.findByName(name).orElse(null);
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

    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public UserProfileDTO getUserProfile(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("Khong tim thay nguoi dung"));
        User_Goal userGoal = userGoalRepository.findByUser_Id(userId);

        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId(user.getId());
        dto.setFullName(user.getName());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setWeight(user.getWeight());
        dto.setHeight(user.getHeight());
        dto.setDesiredHeight(user.getDesiredHeight());
        dto.setDesiredWeight(user.getDesiredWeight());
        dto.setGoalType(userGoal.getGoalType());

        List<UserDiseaseDTO> diseaseDTOs = userDiseaseRepository.findByUser_Id(userId)
            .stream().map(ud -> {
                UserDiseaseDTO dDto = new UserDiseaseDTO();
                dDto.setId(ud.getId());
                dDto.setDiseaseName(ud.getDisease().getDiseaseName());
                dDto.setLevel(ud.getLevel());
                dDto.setDiscoveryDate(ud.getDiscoveryDate().toString());
                dDto.setNote(ud.getNote());
                return dDto;
            }).collect(Collectors.toList());
        dto.setDisease(diseaseDTOs);

        List<UserAllergyDTO> allergyDTOs = userAllergyRepository.findByUser_Id(userId)
            .stream().map(ua -> {
                UserAllergyDTO aDto = new UserAllergyDTO();
                aDto.setId(ua.getId());
                aDto.setIngredientName(ua.getIngredient().getIngredientName());
                aDto.setReaction(ua.getReaction());
                aDto.setNote(ua.getNote());
                return aDto;
            }).collect(Collectors.toList());
            dto.setAllergy(allergyDTOs);

            return dto;
    }
}