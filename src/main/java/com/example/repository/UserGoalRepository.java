package com.example.repository;

import com.example.entity.User_Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGoalRepository extends JpaRepository<User_Goal, Integer> {
}