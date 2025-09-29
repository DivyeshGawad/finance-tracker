package com.demo.finance_tracker_backend.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.demo.finance_tracker_backend.enums.CategoryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "categories")
public class CategoryEntity {

	@Id
	private String id; // MongoDB internal ObjectId

	@Indexed(unique = true)
	private String categoryId; // Custom business ID

	private String userId; // null if default/global category

	private String name;

	private CategoryType type; // INCOME / EXPENSE

	private boolean isDefault; // true = system-provided

	private Instant createdAt;
	private Instant updatedAt;
}