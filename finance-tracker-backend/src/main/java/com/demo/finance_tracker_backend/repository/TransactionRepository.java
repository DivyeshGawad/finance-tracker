package com.demo.finance_tracker_backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.demo.finance_tracker_backend.entity.TransactionEntity;

@Repository
public interface TransactionRepository extends MongoRepository<TransactionEntity, String> {

    // Fetch single transaction by business ID
    Optional<TransactionEntity> findByTransactionId(String transactionId);

    // Fetch all transactions of a user with pagination
    Page<TransactionEntity> findByUserId(String userId, Pageable pageable);

    // Delete transaction by business ID
    void deleteByTransactionId(String transactionId);

    // Fetch transactions of user within date range
    List<TransactionEntity> findByUserIdAndTransactionDateBetween(
    		String userId,
    		LocalDate startDate,
    		LocalDate endDate
    		);
    
    // Fetch transactions by category list within a date range
    List<TransactionEntity> findByUserIdAndCategoryIdAndTransactionDateBetween(
    		String userId,
    		List<String> categoryIds,
    		LocalDate startDate,
    		LocalDate endDate
    		);
    
    // Fetch transactions for single category in a date range
    List<TransactionEntity> findByUserIdAndCategoryIdAndTransactionDateBetween(
    		String userId,
    		String categoryId,
    		LocalDate startDate,
    		LocalDate endDate
    		);
    
}