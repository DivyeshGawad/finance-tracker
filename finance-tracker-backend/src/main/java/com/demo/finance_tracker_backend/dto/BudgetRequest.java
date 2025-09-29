package com.demo.finance_tracker_backend.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetRequest {

	@NotNull(message = "Category is required for a budget")
	private String categoryId;
	
	@NotNull(message = "Amount is required")
	@Positive(message = "Amount must be greater than 0")
	private Double budgetAmount;
    
	@NotNull(message = "Start Date is required")
	private LocalDate startDate;
	
	@NotNull(message = "End Date is required")
	private LocalDate endDate;
	
	private String note;
}