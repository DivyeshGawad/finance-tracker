package com.demo.finance_tracker_backend.dto;

import com.demo.finance_tracker_backend.enums.CategoryType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequest {

	@NotBlank(message = "Category name is required")
	private String name;
	
	@NotNull(message = "Category Type is required")
	private CategoryType type;
}
