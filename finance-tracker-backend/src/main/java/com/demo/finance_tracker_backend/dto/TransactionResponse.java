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
@Builder

@NoArgsConstructor
@AllArgsConstructor
//Avoids Lombok warning about equals/hashCode with RepresentationModel
@EqualsAndHashCode(callSuper = false)
public class TransactionResponse  extends RepresentationModel<TransactionResponse>{

	private String transactionId;
	private String userId;

	private String categoryId;
	private String budgetId;
	private String categoryName;
	private String categoryType;

	private String description;
	private Double amount;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate transactionDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Instant createdAt;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Instant updatedAt;
}
