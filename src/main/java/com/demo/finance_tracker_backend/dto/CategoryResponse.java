package com.demo.finance_tracker_backend.dto;

import java.time.Instant;

import org.springframework.hateoas.RepresentationModel;

import com.demo.finance_tracker_backend.enums.CategoryType;
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
public class CategoryResponse extends RepresentationModel<CategoryResponse>{

	private String categoryId; // Custom business ID
	private String userId;
	private String name; // Category name
	private CategoryType type; // INCOME / EXPENSE
	private boolean isDefault; // System category or user-created

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Instant createdAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Instant updatedAt;

}
