package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.dto.DailyMenuResponseDTO;
import com.example.entity.DailyMenu;
import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyMenuRepository extends JpaRepository<DailyMenu, Integer> {

    Optional<DailyMenu> findByUserIdAndMenuDate(Long userId, LocalDate menuDate);
    @Query("SELECT new com.example.dto.DailyMenuResponseDTO(f.foodName, f.calories, f.imageUrl, md.mealTypeId) " +
           "FROM DailyMenu dm " +
           "JOIN MenuDetail md ON dm.menuId = md.menuId " +
           "JOIN Food f ON md.foodId = f.foodId " +
           "WHERE dm.userId = :userId AND dm.menuDate = :date")
    List<DailyMenuResponseDTO> getDailyMenuDetails(@Param("userId") Long userId, @Param("date") LocalDate date);
}