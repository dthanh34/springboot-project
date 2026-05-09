package com.example.repository;

import com.example.entity.UserFavorite;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Integer> {

    boolean existsByUser_IdAndFood_FoodId(Long userId, Integer foodId);

    @Modifying
    @Transactional
    void deleteByUser_IdAndFood_FoodId(Long userId, Integer foodId);

    List<UserFavorite> findByUser_Id(Long userId);

   @Query(value = "SELECT f.food_name AS foodName, COUNT(*) AS favoriteCount " +
               "FROM user_favorite uf " +
               "JOIN food f ON f.food_id = uf.food_id " +
               "GROUP BY f.food_id, f.food_name " +
               "ORDER BY favoriteCount DESC " +
               "LIMIT 3", nativeQuery = true)
List<Object[]> countTopFoods();
}