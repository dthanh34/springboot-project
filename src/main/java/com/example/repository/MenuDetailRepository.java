package com.example.repository;

import com.example.entity.MenuDetail;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuDetailRepository extends JpaRepository<MenuDetail, Integer> {
    // Lấy chi tiết các món ăn thuộc một mã thực đơn (Menu_id)
    List<MenuDetail> findByMenuId(Integer menuId);
    @Modifying
    @Transactional
    @Query("DELETE FROM MenuDetail md WHERE md.menuId = ?1 AND md.mealTypeId = ?2")
    void deleteByMenuIdAndMealTypeId(Integer menuId, Integer mealTypeId);
}