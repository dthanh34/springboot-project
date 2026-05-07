package com.example.repository;

import com.example.entity.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserGoalRepository extends JpaRepository<UserGoal, Integer> {
    // Lấy mục tiêu sức khỏe mới nhất của người dùng dựa trên ID tăng dần[cite: 18, 30]
    Optional<UserGoal> findTopByUserIdOrderByIdDesc(Long userId);
}