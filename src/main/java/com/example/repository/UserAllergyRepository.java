package com.example.repository;

import com.example.entity.UserAllergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAllergyRepository extends JpaRepository<UserAllergy, Integer> {
}