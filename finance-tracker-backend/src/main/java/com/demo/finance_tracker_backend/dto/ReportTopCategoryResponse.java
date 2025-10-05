package com.demo.finance_tracker_backend.dto;

import com.demo.finance_tracker_backend.enums.CategoryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Top Categories Report
 * Contains category info along with total amount and rank
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportTopCategoryResponse {

    private String categoryId;
    private String categoryName;
    private CategoryType type; // INCOME / EXPENSE
    private double totalAmount;
    private int rank; // Rank among categories based on totalAmount
}
