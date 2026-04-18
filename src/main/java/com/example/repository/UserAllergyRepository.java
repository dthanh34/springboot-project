package com.example.repository;

import com.example.entity.UserAllergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserAllergyRepository extends JpaRepository<UserAllergy, Integer> {
    List <UserAllergy> findByUser_Id(Long id);

    @Query("SELECT ua.ingredient.id FROM UserAllergy ua WHERE ua.user.id = :userId")
    List<Integer> findIngredientIdsByUserId(@Param("userId") Long userId);
}