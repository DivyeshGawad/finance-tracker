package com.demo.finance_tracker_backend.service;

import java.time.LocalDate;

import org.springframework.hateoas.PagedModel;

import com.demo.finance_tracker_backend.dto.BudgetRequest;
import com.demo.finance_tracker_backend.dto.BudgetResponse;

public interface BudgetService {

    /*
     * Create a new budget for a user
     */
    BudgetResponse createBudget(String userId, BudgetRequest request);

    /*
     * Update an existing budget
     */
    BudgetResponse updateBudget(String budgetId, String userId, BudgetRequest request);

    /*
     * Delete a budget
     */
    void deleteBudget(String budgetId, String userId);

    /*
     * Get budget by ID
     */
    BudgetResponse getBudgetById(String budgetId, String userId);

    /*
     * Get all budgets for a user (paged)
     */
    PagedModel<BudgetResponse> getUserBudgetsPaged(
            String userId,
            int page,
            int size,
            String sortBy,
            String direction
    );

    /*
     * Search budgets with filters (date range, category, min/max amount, keyword in note)
     */
    PagedModel<BudgetResponse> searchBudgets(
            String userId,
            String categoryId,
            Double minBudgetAmount,
            Double maxBudgetAmount,
            LocalDate startDate,
            LocalDate endDate,
            String noteKeyword,
            int page,
            int size,
            String sortBy,
            String direction
    );

    /*
     * Update spend amount (called internally when a transaction is created/updated/deleted)
     */
	public void adjustBudgetForCategoryChange(
			String userId, 
			String oldBudgetId,
			String newBUdgetId,
			Double oldAmount,
			Double newAmount,
			String oldCategoryType,
			String newCategoryType
			);
}
