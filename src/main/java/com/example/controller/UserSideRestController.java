package com.example.controller;

import com.example.dto.FavoriteRequest;
import com.example.dto.FoodDTO;
import com.example.dto.UserDTO;
import com.example.service.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserSideRestController {

    @Autowired private RecommendationService recommendationService;
    @Autowired private FoodService foodService;
    @Autowired private FavoriteService favoriteService;
    @Autowired private IngredientService ingredientService;
    @Autowired private DiseaseService diseaseService;

    @Autowired private UserService userService;

    // --- CÁC HÀM CŨ ĐÃ CHUYỂN SANG AUTHENTICATION ---

    @GetMapping("/suggested-foods")
    public ResponseEntity<?> getSuggestions(Authentication authentication) {
        UserDTO currentUser = userService.getCurrentUser(authentication);
        // Lưu ý: recommendationService cần được chỉnh sửa để nhận UserDTO hoặc ID
        return ResponseEntity.ok(recommendationService.getSuggestedFoodsWithMeta(currentUser));
    }

   @GetMapping("/search")
    public ResponseEntity<List<FoodDTO>> search(
        @RequestParam String keyword) {

        return ResponseEntity.ok(
            recommendationService.searchFoodsWithSuitability(keyword)
        );
    }

    @GetMapping("/foods/{id}")
    public ResponseEntity<?> getFoodDetail(@PathVariable Integer id, Authentication authentication) {
        UserDTO currentUser = userService.getCurrentUser(authentication);
        return ResponseEntity.ok(foodService.getFoodDetailForUser(id, currentUser.getId()));
    }

    @PostMapping("/favorites/toggle")
    public ResponseEntity<?> toggleFavorite(@RequestBody FavoriteRequest request,
                                        Authentication authentication) {

        UserDTO currentUser = userService.getCurrentUser(authentication);

        favoriteService.toggleFavorite(currentUser.getId(), request.getFoodId());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/progress-summary")
    public ResponseEntity<?> getProgress(Authentication authentication) {
        UserDTO currentUser = userService.getCurrentUser(authentication);
        return ResponseEntity.ok(userService.getProgressSummary(currentUser.getId()));
    }

    @PostMapping("/update-stats")
    public ResponseEntity<?> updateStats(@RequestBody Map<String, Float> stats, Authentication authentication) {
        UserDTO currentUser = userService.getCurrentUser(authentication);
        userService.updateWeightHeight(currentUser.getId(), stats.get("current_height"), stats.get("current_weight"));
        return ResponseEntity.ok().build();
    }

    // --- 3 API MỚI CHO TRANG PROFILE ---

    @GetMapping("/profile")  
    public ResponseEntity<?> getProfile(Authentication authentication) {
        UserDTO currentUser = userService.getCurrentUser(authentication);
        // Trả về thông tin chi tiết bao gồm userDiseaseIds và userAllergyIds
        return ResponseEntity.ok(userService.getUserFullProfile(currentUser.getId()));
    }

    @PostMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UserDTO profileDTO, Authentication authentication) {
        UserDTO currentUser = userService.getCurrentUser(authentication);
        // Xử lý cập nhật thông tin cá nhân, bệnh lý và dị ứng
        userService.updateFullProfile(currentUser.getId(), profileDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/diseases")
    public ResponseEntity<?> getDiseases() {
        return ResponseEntity.ok(diseaseService.getAllDiseases());
    }

    @GetMapping("/ingredients")
    public ResponseEntity<?> getAllIngredients() {
        // Trả về danh mục toàn bộ nguyên liệu để hiện Dropdown dị ứng
        return ResponseEntity.ok(ingredientService.getAllIngredients());
    }

    @GetMapping("/favorites")
    public ResponseEntity<?> getFavorites(
        @RequestParam(defaultValue = "") String q, 
        Authentication authentication) {
    
        UserDTO currentUser = userService.getCurrentUser(authentication);
    
        // Gọi Service xử lý lọc danh sách
        return ResponseEntity.ok(favoriteService.searchUserFavorites(currentUser.getId(), q));
    }

    @PostMapping("/favorites/remove")
    public ResponseEntity<?> removeFavorite(@RequestParam Integer foodId, Authentication authentication) {
        UserDTO currentUser = userService.getCurrentUser(authentication);
    
        // Sử dụng hàm toggle đã có sẵn (hàm này sẽ xóa nếu bản ghi đã tồn tại)
        favoriteService.toggleFavorite(currentUser.getId(), foodId);
    
        return ResponseEntity.ok().build();
    }
}