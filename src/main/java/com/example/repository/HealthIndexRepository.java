package com.example.repository;

import com.example.entity.Health_Index;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface HealthIndexRepository extends JpaRepository<Health_Index, Long> {
    
    Health_Index findFirstByUserIdOrderByCalculatedAtDesc(Long userId);

    List<Health_Index> findByUserIdOrderByCalculatedAtDesc(Long userId);
}