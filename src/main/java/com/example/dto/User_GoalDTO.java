package com.example.dto;

public class User_GoalDTO {
    private Integer id;
    private Long userId; 
    private String goal_type;
    private float target_calories;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGoal_type() {
        return goal_type;
    }

    public void setGoal_type(String goal_type) {
        this.goal_type = goal_type;
    }

    public float getTarget_calories() {
        return target_calories;
    }

    public void setTarget_calories(float target_calories) {
        this.target_calories = target_calories;
    }
}