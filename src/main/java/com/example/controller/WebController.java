package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @GetMapping("/")
    public String home() {
        return "auth/index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }
    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin/index";
    }

    @GetMapping("/profile")
    public String userProfile(){
        return "user/profile";
    }
    @GetMapping("/dashboard")
    public String dashboard() {
        return "user/index"; 
    }
    @GetMapping("/menu")
    public String menu(){
        return "user/menu";
    }
    @GetMapping("/foods")
    public String foods(){
        return "user/foods";
    }
    
}