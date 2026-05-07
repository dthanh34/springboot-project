package com.example.repository;

import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Tìm người dùng theo email để đăng nhập hoặc kiểm tra tồn tại[cite: 14, 30]
    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    // Kiểm tra email đã được đăng ký chưa[cite: 30]
    boolean existsByEmail(String email);
}