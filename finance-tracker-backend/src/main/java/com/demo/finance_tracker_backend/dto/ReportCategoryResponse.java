package com.demo.finance_tracker_backend.dto;

import com.demo.finance_tracker_backend.enums.CategoryType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportCategoryResponse {

	private String categoryId;
	private String categoryName;
	private CategoryType type;
	private Double totalAmount;
	private int rank;
	
	  // Custom constructor without rank
    public ReportCategoryResponse(String categoryId, String categoryName, CategoryType type, double totalAmount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.type = type;
        this.totalAmount = totalAmount;
    }
}