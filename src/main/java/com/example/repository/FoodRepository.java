package com.example.repository;
import com.example.entity.Food; // 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {

    @Query(value = """
    SELECT f.* FROM Food f
    WHERE (
        (:mealType = 1 AND f.is_breakfast = 1) -- Bữa sáng: Chỉ lấy món ăn sáng
        OR (:mealType <> 1)                    -- Bữa trưa/tối: Lấy món nào cũng được
    )
    AND f.Food_id NOT IN (
        -- Loại bỏ món gây bệnh cho User này
        SELECT fd.Food_id FROM Food_disease fd
        JOIN User_disease ud ON fd.Disease_id = ud.Disease_id
        WHERE ud.User_id = :userId
    )
    AND f.Food_id NOT IN (
        -- Loại bỏ món chứa nguyên liệu User bị dị ứng
        SELECT fi.Food_id FROM Food_Ingredient fi
        JOIN User_Allergy ua ON fi.Ingredient_id = ua.Ingredient_id
        WHERE ua.User_id = :userId
    )
    ORDER BY NEWID() -- Lấy ngẫu nhiên để thực đơn mỗi ngày mỗi khác
    """, nativeQuery = true)
List<Food> findSmartCandidate(@Param("userId") Long userId, @Param("mealType") Integer mealType);
}