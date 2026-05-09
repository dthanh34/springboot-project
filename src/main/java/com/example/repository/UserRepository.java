package com.example.repository;

import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Tìm người dùng theo email để đăng nhập hoặc kiểm tra tồn tại[cite: 14, 30]
    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    // Kiểm tra email đã được đăng ký chưa[cite: 30]
    boolean existsByEmail(String email);
@Query(value = "SELECT COUNT(*) FROM users WHERE create_at >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')", nativeQuery = true)
    long countUsersThisMonth();

    @Query(value = "SELECT COUNT(*) FROM users WHERE create_at >= DATE_FORMAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH), '%Y-%m-01') AND create_at < DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')", nativeQuery = true)
    long countUsersLastMonth();

    @Query(value = "SELECT MONTH(create_at) AS m, COUNT(*) AS c FROM users WHERE YEAR(create_at) = YEAR(CURRENT_DATE) GROUP BY MONTH(create_at)", nativeQuery = true)
    List<Object[]> countNewUsersByMonthInCurrentYear();
}