package com.example.repository;

import com.example.entity.UserFavorite;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Integer> {

    boolean existsByUser_IdAndFood_FoodId(Long userId, Integer foodId);

    @Modifying
    @Transactional
    void deleteByUser_IdAndFood_FoodId(Long userId, Integer foodId);

    List<UserFavorite> findByUser_Id(Long userId);
}