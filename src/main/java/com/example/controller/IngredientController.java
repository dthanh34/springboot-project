package com.example.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.entity.Ingredient;

import com.example.repository.IngredientRepository;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api")
@CrossOrigin
public class IngredientController {
    @Autowired
    private IngredientRepository ingredientRepository;
    @GetMapping("/ingredients")
    public List<Ingredient> getAllIngredients(){
        return ingredientRepository.findAll();
    }
    
}
