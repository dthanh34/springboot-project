package com.example.repository;

import com.example.entity.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserGoalRepository extends JpaRepository<UserGoal, Integer> {
    // Lấy mục tiêu sức khỏe mới nhất của người dùng dựa trên ID tăng dần[cite: 18, 30]
    Optional<UserGoal> findTopByUserIdOrderByIdDesc(Long userId);
@Query(value = "SELECT Goal_type, COUNT(*) FROM user_goal GROUP BY Goal_type ORDER BY COUNT(*) DESC", nativeQuery = true)
    List<Object[]> countGoalsByType();
}