package com.example.repository;

import com.example.entity.User_disease; // Đảm bảo import đúng tên có gạch dưới
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserDiseaseRepository extends JpaRepository<User_disease, Integer> {

    @Query(value = "SELECT disease_id FROM User_disease WHERE user_id = :userId", nativeQuery = true)
    List<Integer> findDiseaseIdsByUserId(@Param("userId") Long userId);

    List<User_disease>  findByUser_Id(Long id);
}