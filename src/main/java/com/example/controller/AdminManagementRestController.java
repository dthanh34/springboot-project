package com.example.controller;

import com.example.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminManagementRestController {

    @Autowired private AdminService adminService;
    @Autowired private FoodService foodService;
    @Autowired private IngredientService ingredientService;
    @Autowired private DiseaseService diseaseService;

    @GetMapping("/dashboard-summary")
    public ResponseEntity<?> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardData());
    }

    
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestParam int page, @RequestParam String keyword, @RequestParam String role) {
        return ResponseEntity.ok(adminService.getUsersPaged(page, keyword, role));
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id, @RequestParam boolean active) {
        adminService.toggleUserStatus(id, active);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ingredients")
    public ResponseEntity<?> getIngredients(
        @RequestParam int page,
        @RequestParam(defaultValue = "") String keyword) {

            return ResponseEntity.ok(
                ingredientService.getIngredients(keyword, page, 10)
            );
    }

    
    @GetMapping("/foods")
    public ResponseEntity<?> getAllFoods() {
        return ResponseEntity.ok(foodService.getAllFoodsForAdmin());
    }

    @PostMapping("/add-food")
    public ResponseEntity<?> addFood(@RequestParam("imageFile") MultipartFile file, @RequestParam Map<String, Object> params) {
        foodService.saveFoodWithImage(file, params);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/foods/{id}")
    public ResponseEntity<?> deleteFood(@PathVariable Integer id) {
        foodService.deleteFood(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/diseases")
    public ResponseEntity<?> getDiseases() {
        return ResponseEntity.ok(diseaseService.getAllDiseases());
    }

    @GetMapping("/compatibility")
    public ResponseEntity<?> getCompatibility() {
        return ResponseEntity.ok(diseaseService.getAllCompatibility());
    }
}