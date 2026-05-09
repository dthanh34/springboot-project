package com.example.repository;

import com.example.entity.DailyMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyMenuRepository extends JpaRepository<DailyMenu, Integer> {
    // Tìm thực đơn của một người dùng trong ngày cụ thể
    Optional<DailyMenu> findByUserIdAndMenuDate(Integer userId, LocalDate menuDate);
 @Query(value = "SELECT COUNT(*) FROM daily_menu WHERE menu_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')", nativeQuery = true)
    long countMenusThisMonth();

    @Query(value = "SELECT COUNT(*) FROM daily_menu WHERE menu_date >= DATE_FORMAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH), '%Y-%m-01') AND menu_date < DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')", nativeQuery = true)
    long countMenusLastMonth();
}