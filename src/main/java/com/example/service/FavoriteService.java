package com.example.service;

import com.example.entity.UserFavorite;
import com.example.entity.User;
import com.example.dto.FavoriteFoodDTO;
import com.example.entity.Food;
import com.example.repository.UserFavoriteRepository;
import com.example.repository.UserRepository;
import com.example.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService {

    @Autowired private UserFavoriteRepository favoriteRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private FoodRepository foodRepo;

    @Transactional
public void toggleFavorite(Long userId, Integer foodId) {

    boolean exists = favoriteRepo.existsByUser_IdAndFood_FoodId(userId, foodId);

    if (exists) {
        favoriteRepo.deleteByUser_IdAndFood_FoodId(userId, foodId);
    } else {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Food food = foodRepo.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food not found"));

        UserFavorite fav = new UserFavorite();
        fav.setUser(user);
        fav.setFood(food);

        favoriteRepo.save(fav);
    }
}

    public List<FavoriteFoodDTO> getUserFavorites(Long userId){
        List<UserFavorite> list = favoriteRepo.findByUser_Id(userId);
        return list.stream().map(fav -> {
            FavoriteFoodDTO dto = new FavoriteFoodDTO();
            dto.setFoodId(fav.getFood().getFoodId());
            dto.setFoodName(fav.getFood().getFoodName());
            dto.setImageUrl(fav.getFood().getImageUrl());
            dto.setCalories(fav.getFood().getCalories());
            return dto;
    }).toList();
    }

    public List<FavoriteFoodDTO> searchUserFavorites(Long userId, String keyword) {
    // 1. Lấy TOÀN BỘ danh sách yêu thích của User từ Database
    List<UserFavorite> allFavs = favoriteRepo.findByUser_Id(userId);

    return allFavs.stream()
            .map(UserFavorite::getFood)
            .filter(f -> {
                // Nếu keyword rỗng -> Lấy tất cả (không loại bỏ món nào)
                // Nếu keyword có chữ -> Chỉ giữ lại món chứa chữ đó (loại các món khác đi)
                if (keyword == null || keyword.trim().isEmpty()) return true;
                return f.getFoodName().toLowerCase().contains(keyword.toLowerCase().trim());
            })
            .map(food -> {
                FavoriteFoodDTO dto = new FavoriteFoodDTO();
                dto.setFoodId(food.getFoodId());
                dto.setFoodName(food.getFoodName());
                dto.setImageUrl(food.getImageUrl());
                dto.setCalories(food.getCalories() != null ? food.getCalories().doubleValue() : 0.0);
                return dto;
            })
            .toList();
}
}