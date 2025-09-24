package com.demo.finance_tracker_backend.service;

import java.util.List;

import org.springframework.hateoas.PagedModel;

import com.demo.finance_tracker_backend.dto.CategoryRequest;
import com.demo.finance_tracker_backend.dto.CategoryResponse;

public interface CategoryService {

	CategoryResponse createCategory(String userId, CategoryRequest request);

	CategoryResponse updateCategory(String categoryId, CategoryRequest request, String userId);

	void deleteCategory(String categoryId, String userId);

	List<CategoryResponse> getUserCategories(String userId);  // only user-created categories

//    PagedModel<CategoryResponse> getAllCategoriesPaged(String userId, int page, int size, String sortBy, String direction);  // user + default categories
    
	CategoryResponse getCategoryById(String categoryId, String userId);

	boolean isCategoryValidForUser(String categoryId, String userId);
	
//	PagedModel<CategoryResponse> searchCategories(String userId, String name, String type, int page, int size, String sortBy, String direction);

	/**
	 * Get all categories (default + user-created)
	 */
	PagedModel<CategoryResponse> getCategoriesPaged(String userId, String name, String type,
            int page, int size, String sortBy, String direction);
}