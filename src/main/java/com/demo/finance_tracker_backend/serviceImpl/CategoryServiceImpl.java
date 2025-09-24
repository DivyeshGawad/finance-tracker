package com.demo.finance_tracker_backend.serviceImpl;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.finance_tracker_backend.assembler.CategoryResponseAssembler;
import com.demo.finance_tracker_backend.dto.CategoryRequest;
import com.demo.finance_tracker_backend.dto.CategoryResponse;
import com.demo.finance_tracker_backend.entity.CategoryEntity;
import com.demo.finance_tracker_backend.exception.BadRequestException;
import com.demo.finance_tracker_backend.exception.ResourceNotFoundException;
import com.demo.finance_tracker_backend.exception.UnauthorizedException;
import com.demo.finance_tracker_backend.repository.CategoryRepository;
import com.demo.finance_tracker_backend.service.CategoryService;
import com.demo.finance_tracker_backend.util.IdGeneratorUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final CategoryResponseAssembler assembler;
	private final PagedResourcesAssembler<CategoryEntity> pagedResourcesAssembler;
	
	/**
	 * Create a new category for the user
	 */
	@Transactional
	@Override
	public CategoryResponse createCategory(String userId, CategoryRequest request) {

		// Check for duplicate category name for this user
		boolean exists = categoryRepository.findByUserId(userId).stream()
				.anyMatch(cat -> cat.getName().equalsIgnoreCase(request.getName()));

		if (exists) {
			throw new DuplicateKeyException("Category with the same name already exists for this user");
		}

		Instant now = Instant.now();

		CategoryEntity category = CategoryEntity.builder().categoryId(IdGeneratorUtil.generatePrefixedId("CAT"))
				.name(request.getName()).type(request.getType()).userId(userId).isDefault(false).createdAt(now)
				.updatedAt(now).build();

		categoryRepository.save(category);
		log.info("Created new Category '{}' for user {}", category.getName(), userId);

		return assembler.toModel(category);
	}

	/**
	 * Update category for the user
	 */
	@Transactional
	@Override
	public CategoryResponse updateCategory(String categoryId, CategoryRequest request, String userId) {

		CategoryEntity category = categoryRepository.findByCategoryId(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category Not Found."));

		if (category.isDefault()) {
			throw new BadRequestException("Default Categories cannot be updated.");
		}

		if (!category.getUserId().equals(userId)) {
			throw new UnauthorizedException("You are not allowed to update this category");
		}

		// Check duplicate name
		boolean exists = categoryRepository.findByUserId(userId).stream()
			    .anyMatch(cat -> !cat.getCategoryId().equals(categoryId) && 
			                     cat.getName().equalsIgnoreCase(request.getName()));

		if (exists) {
			throw new DuplicateKeyException("Category with the same name already exists for this user");
		}

		category.setName(request.getName());
		category.setType(request.getType());
		category.setUpdatedAt(Instant.now());

		categoryRepository.save(category);
		log.info("Updated Category {} for user {}", categoryId, userId);

		return assembler.toModel(category);
	}

	/**
	 * Delete category
	 */
	@Override
	public void deleteCategory(String categoryId, String userId) {

		CategoryEntity category = categoryRepository.findByCategoryId(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category Not Found."));

		if (category.isDefault()) {
			throw new BadRequestException("Default Categories cannot be deleted.");
		}

		if (!category.getUserId().equals(userId)) {
			throw new UnauthorizedException("You are not allowed to delete this category");
		}

		categoryRepository.deleteByCategoryId(categoryId);
	}

	/**
	 * Get all categories for a user (user-created only)
	 */
	@Override
	public List<CategoryResponse> getUserCategories(String userId) {
	    return categoryRepository.findByUserId(userId).stream()
	            .map(assembler::toModel)   // Use assembler here
	            .collect(Collectors.toList());
	}


	/**
	 * Get category by ID scoped to user
	 */
	@Override
	public CategoryResponse getCategoryById(String categoryId, String userId) {

		CategoryEntity category = categoryRepository.findByCategoryId(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found"));

		if (!category.isDefault() && !category.getUserId().equals(userId)) {
			throw new UnauthorizedException("You are not allowed to access this category");
		}

		return assembler.toModel(category);
	}

	/**
	 * Validate if category belongs to user or is default
	 */
	@Override
	public boolean isCategoryValidForUser(String categoryId, String userId) {

		return categoryRepository.findByCategoryId(categoryId)
				.filter(cat -> cat.isDefault() || cat.getUserId().equals(userId)).isPresent();
	}

	/**
	 * Get all categories (default + user-created)
	 */
//	@Override
//	public PagedModel<CategoryResponse> getAllCategoriesPaged(String userId, int page, int size, String sortBy, String direction){
//		Sort sort = "asc".equalsIgnoreCase(direction)
//				? Sort.by(sortBy).ascending()
//				: Sort.by(sortBy).descending();
//		
//		Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
//		
//		// Fetch default + user categories
//		List<CategoryEntity> allCategories = new ArrayList<>();
//		allCategories.addAll(categoryRepository.findByIsDefaultTrue());
//		allCategories.addAll(categoryRepository.findByUserId(userId));
//		
//		// Convert to Page Manually
//		int start = (int) pageable.getOffset();
//		int end = Math.min(start + pageable.getPageSize(), allCategories.size());
//		
//		Page<CategoryEntity> pageResult = new PageImpl<>(allCategories.subList(start, end), pageable, allCategories.size());
//		
//		return pagedResourcesAssembler.toModel(pageResult, assembler);
//	}
	
	@Override
	public PagedModel<CategoryResponse> getCategoriesPaged(String userId, String name, String type,
	                                                      int page, int size, String sortBy, String direction) {
	    String safeName = (name == null || name.isEmpty()) ? ".*" : name;
	    String safeType = (type == null || type.isEmpty()) ? ".*" : type;

	    Sort sort = "asc".equalsIgnoreCase(direction)
	            ? Sort.by(sortBy).ascending().and(Sort.by("categoryId").ascending())
	            : Sort.by(sortBy).descending().and(Sort.by("categoryId").descending());

	    Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);

	    Page<CategoryEntity> pageResult = categoryRepository.searchCategories(userId, safeName, safeType, pageable);

	    return pagedResourcesAssembler.toModel(pageResult, assembler);
	}


	/**
	 * Search Categories by Name or Type or Both using Pagination and Sort
	 */
//	@Override
//	public PagedModel<CategoryResponse> searchCategories(String userId, String name, String type, int page, int size,
//			String sortBy, String direction) {
//
//		// Defensive defaults
//		String safeName = (name == null || name.isEmpty()) ? ".*" : name;
//		String safeType = (type == null || type.isEmpty()) ? ".*" : type;
//
//		Sort sort = "asc".equalsIgnoreCase(direction) 
//				? Sort.by(sortBy).ascending() 
//				: Sort.by(sortBy).descending();
//		Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
//
//		// Defensive defaults: if name/type null → match all (handled in repository impl
//		// / query builder)
//		Page<CategoryEntity> pageResult = categoryRepository.searchCategories(userId, safeName,safeType, pageable);
//
//		return pagedResourcesAssembler.toModel(pageResult, assembler);
//	}
}