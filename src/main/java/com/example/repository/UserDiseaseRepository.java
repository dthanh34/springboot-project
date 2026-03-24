package com.example.repository;

import com.example.entity.User_disease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDiseaseRepository extends JpaRepository<User_disease, Integer> {
}