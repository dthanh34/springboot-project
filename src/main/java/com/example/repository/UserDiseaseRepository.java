package com.example.repository;

import com.example.entity.UserDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserDiseaseRepository extends JpaRepository<UserDisease, Integer> {
    // Lấy danh sách các bản ghi bệnh lý của một người dùng
    List<UserDisease> findByUserId(Long userId);

    // Xóa toàn bộ dữ liệu bệnh lý cũ của User 
    void deleteByUserId(Long userId);
}