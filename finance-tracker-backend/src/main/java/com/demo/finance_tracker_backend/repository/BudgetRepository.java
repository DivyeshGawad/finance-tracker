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

    // Fetch budgets by category for a user
    Page<BudgetEntity> findByUserIdAndCategoryId(String userId, String categoryId, Pageable pageable);

    // Fetch budget by user+category (single budget for category)
    Optional<BudgetEntity> findByUserIdAndCategoryId(String userId, String categoryId);

    // ✅ Find budgets by date overlap (used for active budgets at transaction time)
    Optional<BudgetEntity> findByUserIdAndCategoryIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String userId,
            String categoryId,
            LocalDate transactionDate,
            LocalDate transactionDate2
    );

    void deleteByBudgetId(String budgetId);
}
