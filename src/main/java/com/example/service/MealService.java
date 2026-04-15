package com.example.service;

import com.example.entity.Food;
import com.example.dto.MealRequestDTO;
import com.example.repository.FoodRepository;
import com.example.repository.UserDiseaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class MealService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private UserDiseaseRepository userDiseaseRepo;

    public List<Food> generateMenu(MealRequestDTO request) {
        // 1. Lấy danh sách bệnh (Xử lý mảng bệnh rỗng để tránh lỗi SQL IN clause)
        List<Integer> diseaseIds = userDiseaseRepo.findDiseaseIdsByUserId(request.getUserId());
        if (diseaseIds == null) {
            diseaseIds = new ArrayList<>();
        }
        if (diseaseIds.isEmpty() && (request.getAcuteDiseaseIds() == null || request.getAcuteDiseaseIds().isEmpty())) {
            diseaseIds.add(-1); 
        }
        if (request.getAcuteDiseaseIds() != null) {
            diseaseIds.addAll(request.getAcuteDiseaseIds());
        }

        // 2. Tính toán mục tiêu Calo cho mỗi bữa
        // Nếu TDEE truyền lên bị null, mặc định lấy 2000 kcal
        double targetCaloPerMeal = (request.getTdee() != null ? request.getTdee() : 2000.0) / 3;
        Random rand = new Random();

        // 3. Phân loại theo Chế độ ăn
        if (request.isFamilyMeal()) {
            // --- CHẾ ĐỘ CƠM MÂM ---
            // Lấy danh sách các món an toàn theo dish_type của Thành (1:Mặn, 2:Rau, 3:Canh, 4:Cơm)
            List<Food> listMan = foodRepository.findSafeFoods(diseaseIds, 1);
            List<Food> listRau = foodRepository.findSafeFoods(diseaseIds, 2);
            List<Food> listCanh = foodRepository.findSafeFoods(diseaseIds, 3);
            List<Food> listCom = foodRepository.findSafeFoods(diseaseIds, 4);

            List<Food> bestTray = new ArrayList<>();
            double minDiff = Double.MAX_VALUE;

            // Thuật toán: Thử bốc ngẫu nhiên 10 lần để tìm mâm cơm gần với Target Calo nhất
            for (int i = 0; i < 10; i++) {
                List<Food> currentTray = new ArrayList<>();
                if (!listCom.isEmpty()) currentTray.add(listCom.get(rand.nextInt(listCom.size())));
                if (!listMan.isEmpty()) currentTray.add(listMan.get(rand.nextInt(listMan.size())));
                if (!listRau.isEmpty()) currentTray.add(listRau.get(rand.nextInt(listRau.size())));
                if (!listCanh.isEmpty()) currentTray.add(listCanh.get(rand.nextInt(listCanh.size())));

                double totalCalo = currentTray.stream().mapToDouble(f -> f.getCalories()).sum();
                
                // Nếu tổng calo vẫn thấp hơn target quá nhiều, thử bốc thêm 1 món mặn nữa nếu có thể
                if (totalCalo < (targetCaloPerMeal - 150) && listMan.size() > 1) {
                    Food extraMan = listMan.get(rand.nextInt(listMan.size()));
                    if (!currentTray.contains(extraMan)) {
                        currentTray.add(extraMan);
                        totalCalo += extraMan.getCalories();
                    }
                }

                if (Math.abs(totalCalo - targetCaloPerMeal) < minDiff) {
                    minDiff = Math.abs(totalCalo - targetCaloPerMeal);
                    bestTray = new ArrayList<>(currentTray);
                }
            }
            return bestTray;

        } else {
            // --- CHẾ ĐỘ ĂN QUÁN (dish_type = 0) ---
            List<Food> outFoods = foodRepository.findSafeFoods(diseaseIds, 0);
            
            // Lọc món có Calo hợp lý (không quá thấp so với TDEE của Nam)
            List<Food> filtered = outFoods.stream()
                    .filter(f -> f.getCalories() >= (targetCaloPerMeal - 300))
                    .collect(Collectors.toList());

            List<Food> result = new ArrayList<>(filtered.isEmpty() ? outFoods : filtered);
            Collections.shuffle(result);
            
            // Trả về 3 món ăn quán khác nhau cho 3 card sáng/trưa/tối
            return result.stream().limit(3).collect(Collectors.toList());
        }
    }

    private Food getRandomFood(List<Integer> ids, int type) {
        List<Food> safeFoods = foodRepository.findSafeFoods(ids, type);
        if (safeFoods == null || safeFoods.isEmpty()) return null;
        return safeFoods.get(new Random().nextInt(safeFoods.size()));
    }
}