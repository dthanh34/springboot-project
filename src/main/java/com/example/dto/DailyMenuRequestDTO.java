package com.example.dto; // Thành nhớ sửa lại package cho đúng project của mình nhé

import lombok.Data;
import java.util.List;

@Data
public class DailyMenuRequestDTO {
    private Long userId;
    private String menuDate;
    private List<MealItemDTO> details;

    @Data
    public static class MealItemDTO {
        private Integer foodId;
        private Integer mealTypeId; 
    }
}