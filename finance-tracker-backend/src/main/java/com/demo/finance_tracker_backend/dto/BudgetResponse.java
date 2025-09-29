package com.demo.finance_tracker_backend.dto;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Avoids Lombok warning about equals/hashCode with RepresentationModel
@EqualsAndHashCode(callSuper = false)
public class BudgetResponse extends RepresentationModel<BudgetResponse> {

	private String budgetId;
	private String userId;
	private String categoryId;
	private String categoryName;
	private String categoryType;
	private Double budgetAmount;
	private Double spendAmount;
	private String note;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate startDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate endDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Instant createdAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Instant updatedAt;
	
	 // ✅ Derived / calculated fields (not stored in DB)
    private Double percentUsed; // e.g., (spendAmount / budgetAmount) * 100
    private String status;      // e.g., "ON_TRACK", "NEARING_LIMIT", "EXCEEDED"
}
