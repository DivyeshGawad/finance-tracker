package com.demo.finance_tracker_backend.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.demo.finance_tracker_backend.entity.BudgetEntity;

@Repository
public interface BudgetRepository extends MongoRepository<BudgetEntity, String> {
	
	Optional<BudgetEntity> findByBudgetId(String budgetId);
	
	 // Fetch all budgets for a specific user (paged)
	Page<BudgetEntity> findByUserId(String userId, Pageable pageable);
	
	 // Fetch budgets by category (for a user)
	Page<BudgetEntity> findByUserIdAndCategoryId(String categoryId, String userId, Pageable pageable);
	
	 // Fetch budgets by category (for a user)
	Optional<BudgetEntity> findByUserIdAndCategoryId(String userId, String categoryId);
	
	 // ✅ New method: Find budget for a user/category within date range
    Optional<BudgetEntity> findByUserIdAndCategoryIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String userId,
            String categoryId,
            LocalDate transactionDateStart,
            LocalDate transactionDateEnd
    );
    
	void deleteByBudgetId(String budgetId);
}
