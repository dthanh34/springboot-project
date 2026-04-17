package com.example.repository;

import com.example.entity.UserAllergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserAllergyRepository extends JpaRepository<UserAllergy, Integer> {
    List <UserAllergy> findByUser_Id(Long id);
}