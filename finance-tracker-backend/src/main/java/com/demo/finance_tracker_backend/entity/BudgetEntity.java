package com.demo.finance_tracker_backend.entity;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "budget")
public class BudgetEntity {

	@Id
	private String id;

	@Indexed(unique = true)
	private String budgetId;

	private String userId;

	private String categoryId;

	private Double budgetAmount; // allocated amount
	
	private Double spendAmount; // updated in real-time based on transactions

	private LocalDate startDate;

	private LocalDate endDate;

	private String note;

	@CreatedDate
	private Instant createdAt;

	@LastModifiedDate
	private Instant updatedAt;
}
