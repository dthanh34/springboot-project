package com.example.repository;

import com.example.entity.UserAllergy;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAllergyRepository extends JpaRepository<UserAllergy, Integer> {
    List<UserAllergy> findByUserId(Long userId);
    // Xóa toàn bộ dữ liệu dị ứng cũ của User để cập nhật mới
    void deleteByUserId(Long userId);
}