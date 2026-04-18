package com.example.service;

import com.example.dto.DailyMenuRequestDTO;
import com.example.dto.DailyMenuResponseDTO;
import com.example.dto.MealRequestDTO;
import com.example.entity.DailyMenu;
import com.example.entity.Food;
import com.example.repository.DailyMenuRepository;
import com.example.repository.FoodRepository;
import com.example.repository.MenuDetailRepository;
import com.example.repository.UserAllergyRepository;
import com.example.repository.UserDiseaseRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class MealService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private UserDiseaseRepository userDiseaseRepo;

    @Autowired
    private MenuDetailRepository menuDetailRepo;

    @Autowired
    private UserAllergyRepository userAllergyRepo;

    @Autowired
    private DailyMenuRepository dailyMenuRepo;

    @PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    public List<Food> suggestMeal(Long userId, boolean isEatOut, Double targetCal, List<Integer> acuteDiseaseIds) {
        List<Integer> diseaseIds = getBlacklistDiseases(userId, acuteDiseaseIds);
        List<Integer> allergyIds = getBlacklistAllergies(userId);

        if (isEatOut) {
            Food dish = foodRepository.findRandomSafeEatOut(diseaseIds, allergyIds, targetCal);
            return dish != null ? Collections.singletonList(dish) : new ArrayList<>();
        } else {
            return pickRandomHomeMeal(diseaseIds, allergyIds, targetCal);
        }
    }

    public List<Food> getAllSafeFoods(MealRequestDTO request) {
        List<Integer> diseaseIds = getBlacklistDiseases(request.getUserId(), request.getAcuteDiseaseIds());
        List<Integer> allergyIds = getBlacklistAllergies(request.getUserId());
        return foodRepository.findSafeFoods(diseaseIds, allergyIds, null, null);
    }

    @Transactional
    public void saveUserDailyMenu(DailyMenuRequestDTO request) {
        LocalDate parsedDate = LocalDate.parse(request.getMenuDate());

        DailyMenu menu = dailyMenuRepo.findByUserIdAndMenuDate(request.getUserId(), parsedDate)
            .orElseGet(() -> {
                DailyMenu newMenu = new DailyMenu();
                newMenu.setUserId(request.getUserId());
                newMenu.setMenuDate(parsedDate);
                newMenu.setTotalCalories(0.0);
                newMenu.setStatus("0");
                return dailyMenuRepo.save(newMenu);
            });

        menuDetailRepo.deleteByMenuId(menu.getMenuId());
        entityManager.flush();
        entityManager.clear();

        double totalCal = 0;
        if (request.getDetails() != null) {
            for (DailyMenuRequestDTO.MealItemDTO item : request.getDetails()) {
                if (item.getFoodId() == null) {
                    System.out.println("❌ foodId null tại item: " + item);
                    continue; 
                }
                Food food = foodRepository.findById(item.getFoodId()).orElse(null);
                if (food != null) {
                    totalCal += (food.getCalories() != null) ? food.getCalories() : 0;
                
                    menuDetailRepo.insertDetail(
                    menu.getMenuId(), 
                    food.getFoodId(), 
                    item.getMealTypeId()
                );
                }
            }
        }

        menu.setTotalCalories(totalCal);
        dailyMenuRepo.save(menu);
    }
    private List<Integer> getBlacklistDiseases(Long userId, List<Integer> acuteIds) {
        List<Integer> ids = userDiseaseRepo.findDiseaseIdsByUserId(userId);
        if (ids == null) ids = new ArrayList<>();
        if (acuteIds != null) ids.addAll(acuteIds);
        if (ids.isEmpty()) ids.add(-1);
        return ids;
    }

    private List<Integer> getBlacklistAllergies(Long userId) {
        List<Integer> ids = userAllergyRepo.findIngredientIdsByUserId(userId);
        if (ids == null || ids.isEmpty()) return Collections.singletonList(-1);
        return ids;
    }

    private List<Food> pickRandomHomeMeal(List<Integer> diseaseIds, List<Integer> allergyIds, Double targetMax) {
        List<Food> man = foodRepository.findSafeFoodsByType(diseaseIds, allergyIds, 1);
        List<Food> xao = foodRepository.findSafeFoodsByType(diseaseIds, allergyIds, 2);
        List<Food> canh = foodRepository.findSafeFoodsByType(diseaseIds, allergyIds, 3);
        List<Food> khac = foodRepository.findSafeFoodsByType(diseaseIds, allergyIds, 4);

        Collections.shuffle(man); Collections.shuffle(xao); 
        Collections.shuffle(canh); Collections.shuffle(khac);

        for (Food m : man) {
            for (Food x : xao) {
                for (Food c : canh) {
                    for (Food k : khac) {
                        double total = m.getCalories() + x.getCalories() + c.getCalories() + k.getCalories();
                        if (total <= targetMax && total >= targetMax * 0.7) {
                            return Arrays.asList(m, x, c, k);
                        }
                    }
                }
            }
        }
        return new ArrayList<>();
    }
    public List<DailyMenuResponseDTO> getMenuByDate(Long userId, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        return dailyMenuRepo.getDailyMenuDetails(userId, date);
    }
    
}