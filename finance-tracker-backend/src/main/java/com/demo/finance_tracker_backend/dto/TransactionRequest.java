package com.demo.finance_tracker_backend.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransactionRequest {

	@NotNull(message = "Category Id required")
	private String categoryId;

	private String budgetId;
	
	private String description;
	
	@NotNull(message = "Amount is required")
	@Positive(message = "Amount must be greater than 0")
	private Double amount;
	
	@NotNull(message = "Transaction Date required")
	private LocalDate transactionDate;
}
