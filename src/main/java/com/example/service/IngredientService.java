package com.example.service;

import com.example.dto.IngredientDTO;
import com.example.entity.Ingredient;
import com.example.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IngredientService {

    @Autowired 
    private IngredientRepository ingredientRepo;

    public Map<String, Object> getIngredients(String keyword, int page, int size) {

        Page<Ingredient> ingredientPage;

        if (keyword == null || keyword.trim().isEmpty()) {
            ingredientPage = ingredientRepo.findAll(PageRequest.of(page - 1, size));
        } else {
            ingredientPage = ingredientRepo.findByIngredientNameContainingIgnoreCase(
                keyword, PageRequest.of(page - 1, size));
        }

        var list = ingredientPage.getContent().stream().map(i -> {
            IngredientDTO dto = new IngredientDTO();
            dto.setIngredientId(i.getIngredientId());
            dto.setIngredientName(i.getIngredientName());
            dto.setCategory(i.getCategory());
            dto.setCalories(i.getCalories());
            dto.setProtein(i.getProtein());
            dto.setFat(i.getFat());
            dto.setCarbohydrate(i.getCarbohydrate());
            return dto;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("ingredients", list);
        response.put("currentPage", page);
        response.put("totalPages", ingredientPage.getTotalPages());
        response.put("totalRecords", ingredientPage.getTotalElements());

        return response;
   }

    @Transactional
    public void saveIngredient(Ingredient ing) {
        ingredientRepo.save(ing);
    }

    @Transactional
    public void deleteIngredient(Integer id) {
        ingredientRepo.deleteById(id);
    }

    public Ingredient getById(Integer id) {
        return ingredientRepo.findById(id).orElse(null);
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepo.findAll(); 
    }
}