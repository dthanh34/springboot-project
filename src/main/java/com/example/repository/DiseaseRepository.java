package com.example.repository;

import com.example.entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DiseaseRepository extends JpaRepository<Disease, Integer> {
    @Query(value = "SELECT * FROM Disease WHERE disease_type = N'Ngắn ngày'", nativeQuery = true)
    List<Disease> findByDiseaseType(String type);
}