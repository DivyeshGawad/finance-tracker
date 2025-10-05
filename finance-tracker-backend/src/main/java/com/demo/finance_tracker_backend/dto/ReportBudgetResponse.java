package com.demo.finance_tracker_backend.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NegativeOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NegativeOrZero
public class ReportBudgetResponse {

	private String budgetId;
	private String categoryId;
	private String categoryName;
	private double spentAmount;
	private double remainingAmount;
	private double totalBudget;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate startDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate endDate;

	// new field for % utilization
	private double utilizationPercentage;
}