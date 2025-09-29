package com.demo.finance_tracker_backend.controller;

import java.util.List;

import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.finance_tracker_backend.dto.ApiResponse;
import com.demo.finance_tracker_backend.dto.CategoryRequest;
import com.demo.finance_tracker_backend.dto.CategoryResponse;
import com.demo.finance_tracker_backend.security.CurrentUser;
import com.demo.finance_tracker_backend.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/categories")
@Slf4j
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;
	private final CurrentUser currentUser;
	
	@PostMapping
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request){
		String userId = currentUser.getUserId();
		
		CategoryResponse response = categoryService.createCategory(userId, request);
		
		log.info("Category creared {} by userId={}", request.getName(),userId);
		
		return ResponseEntity.ok(ApiResponse.success("Category Created Successfully", response));
	}
	
	@PutMapping("/{categoryId}")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
			@PathVariable String categoryId,
			@Valid @RequestBody CategoryRequest request
			){
		String userId = currentUser.getUserId();
		CategoryResponse response = categoryService.updateCategory(categoryId, request, userId);
		
		log.info("Category Updated: {} by userId={}", categoryId, userId);
		
		return ResponseEntity.ok(ApiResponse.success("Category Updated Successfully",response));
	}
	
	@DeleteMapping("/{categoryId}")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable String categoryId) {
		String userId = currentUser.getUserId();	
		categoryService.deleteCategory(categoryId, userId);
		
		log.info("Category deleted {} by userId = {}", categoryId, userId);
		
		return ResponseEntity.ok(ApiResponse.success("Category Deleted Successfully"));
	}
	
	@GetMapping("/myCategories")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<ApiResponse<List<CategoryResponse>>> getUserCategories(){
		String userId = currentUser.getUserId();
		List<CategoryResponse> list = categoryService.getUserCategories(userId);
		
		return ResponseEntity.ok(ApiResponse.success("User Categories Fetch Successfully", list));
	}
	
//	@GetMapping
//	@PreAuthorize("hasAnyRole('USER','ADMIN')")
//	public ResponseEntity<ApiResponse<PagedModel<CategoryResponse>>> getAllCategories(
//	        @RequestParam(defaultValue = "0") int page,
//	        @RequestParam(defaultValue = "10") int size,
//	        @RequestParam(defaultValue = "createdAt") String sortBy,
//	        @RequestParam(defaultValue = "desc") String direction
//	) {
//	    String userId = currentUser.getUserId();
//	    PagedModel<CategoryResponse> results = categoryService.getAllCategoriesPaged(userId, page, size, sortBy, direction);
//	    return ResponseEntity.ok(ApiResponse.success("All Categories fetched successfully", results));
//	}
	
	@GetMapping("/all")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<ApiResponse<PagedModel<CategoryResponse>>> getCategories(
	        @RequestParam(required = false) String name,
	        @RequestParam(required = false) String type,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "createdAt") String sortBy,
	        @RequestParam(defaultValue = "desc") String direction
	) {
	    String userId = currentUser.getUserId();
	    PagedModel<CategoryResponse> results = categoryService.getCategoriesPaged(
	            userId, name, type, page, size, sortBy, direction
	    );

	    return ResponseEntity.ok(ApiResponse.success("Categories fetched successfully", results));
	}


	
	@GetMapping("/{categoryId}")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable String categoryId){
		String userId = currentUser.getUserId();
		CategoryResponse response = categoryService.getCategoryById(categoryId, userId);
		
		return ResponseEntity.ok(ApiResponse.success("Category fetch successfully", response));
	}
	
	// Search + Pagination
//	@GetMapping("/search")
//	@PreAuthorize("hasAnyRole('USER','ADMIN')")
//	public ResponseEntity<ApiResponse<PagedModel<CategoryResponse>>> searchCategories(
//			@RequestParam(required = false) String name,
//			@RequestParam(required = false) String type,
//			@RequestParam(defaultValue = "0") int page,
//			@RequestParam(defaultValue = "10") int size,
//			@RequestParam(defaultValue = "createdAt") String sortBy,
//			@RequestParam(defaultValue = "desc")String direction
//			){
//		String userId = currentUser.getUserId();
//		PagedModel<CategoryResponse> results = categoryService.searchCategories(userId, name, type, page, size, sortBy, direction);
//		
//		return ResponseEntity.ok(ApiResponse.success("Categories fetch successfully", results));
//	}
}
