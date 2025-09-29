package com.demo.finance_tracker_backend.controller;

import java.time.LocalDate;

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
import com.demo.finance_tracker_backend.dto.BudgetRequest;
import com.demo.finance_tracker_backend.dto.BudgetResponse;
import com.demo.finance_tracker_backend.security.CurrentUser;
import com.demo.finance_tracker_backend.service.BudgetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Slf4j
public class BudgetController {

	private final BudgetService budgetService;
	private final CurrentUser currentUser;
	
    // ✅ Create Budget
	@PostMapping()
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<ApiResponse<BudgetResponse>> createBudget(@Valid @RequestBody BudgetRequest request){
		
		String userId = currentUser.getUserId();
		
		BudgetResponse response = budgetService.createBudget(userId, request);
		
		return ResponseEntity.ok(ApiResponse.success("Budget Created Successfully", response));
	}
	
	// ✅ Update Budget
	@PutMapping("/{budgetId}")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<ApiResponse<BudgetResponse>> updateBudget(
			@PathVariable String budgetId,
			@Valid @RequestBody BudgetRequest request
			){
		String userId = currentUser.getUserId();
		
		BudgetResponse response = budgetService.updateBudget(budgetId, userId, request);
		
		return ResponseEntity.ok(ApiResponse.success("Budget Updated Successfully", response));
	}
	
	// ✅ Delete Budget
	@DeleteMapping("/{budgetId}")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<ApiResponse<Void>> deleteBudget(@PathVariable String budgetId){
		
		String userId = currentUser.getUserId();
		
		budgetService.deleteBudget(budgetId, userId);
		
		return ResponseEntity.ok(ApiResponse.success("Budget Deleted Successfully"));
	}
	
	// ✅ Get Budget By BudgetId
	@GetMapping("/{budgetId}")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<ApiResponse<BudgetResponse>> getBudgetByBudgetId(@PathVariable String budgetId){
		
		String user =  currentUser.getUserId();
		BudgetResponse response = budgetService.getBudgetById(budgetId, user);
		
		return ResponseEntity.ok(ApiResponse.success("Budget Fetched SUccessfully", response));
	}
	
	 // ✅ Get Paged Budgets
	@GetMapping("/myBudgets")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<ApiResponse<PagedModel<BudgetResponse>>> getUserPagedBudget(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(defaultValue = "budgetId") String sortBy,
			@RequestParam(defaultValue = "asc") String direction
			){
		
		String userId = currentUser.getUserId();
		
		PagedModel<BudgetResponse> response = budgetService.getUserBudgetsPaged(userId, page, size, sortBy, direction);
		
		return ResponseEntity.ok(ApiResponse.success("Successfully fetched all transaction for user ", response));
	}
	
	// Search Budget
	@GetMapping("/search")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<ApiResponse<PagedModel<BudgetResponse>>> searchBudgets(
			@RequestParam(required = false) String categoryId,
			@RequestParam(required = false) Double minBudgetAmount,
			@RequestParam(required = false) Double maxBudgetAmount,
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate,
			@RequestParam(required = false) String noteKeyword,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(defaultValue = "budgetId") String sortBy,
			@RequestParam(defaultValue = "asc") String direction
			){
		String userId = currentUser.getUserId();
		
		LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
		LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
		
		PagedModel<BudgetResponse> pagedModel = budgetService.searchBudgets(userId, categoryId, minBudgetAmount, maxBudgetAmount, start, end, noteKeyword, page, size, sortBy, direction);
		
	    if (pagedModel.getMetadata().getTotalElements() == 0) {
	        return ResponseEntity.ok(ApiResponse.error("Bidget Not Found.", null));
	    }
	    return ResponseEntity.ok(ApiResponse.success("Budgets fetched successfully", pagedModel));
	}
}