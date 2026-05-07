package com.example.repository;

import com.example.entity.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealTypeRepository extends JpaRepository<MealType, Integer> {
    List<MealType> findAllByOrderByMealTypeIdAsc();
}

