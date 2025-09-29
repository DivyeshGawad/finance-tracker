package com.demo.finance_tracker_backend.service;

import java.time.LocalDate;

import org.springframework.hateoas.PagedModel;

import com.demo.finance_tracker_backend.dto.TransactionRequest;
import com.demo.finance_tracker_backend.dto.TransactionResponse;

public interface TransactionService {

    // Create a new transaction
    TransactionResponse createTransaction(String userId, TransactionRequest request);

    // Update an existing transaction
    TransactionResponse updateTransaction(String transactionId, TransactionRequest request, String userId);

    // Delete a transaction
    void deleteTransaction(String transactionId, String userId);

    // Fetch a single transaction by its ID
    TransactionResponse getTransactionById(String transactionId, String userId);

    // Fetch all transactions of a user (optional pagination)
    PagedModel<TransactionResponse> getUserTransactionsPaged(
            String userId,
            int page,
            int size,
            String sortBy,
            String direction
    );

    // Search transactions dynamically with filters, pagination, and sorting
    public PagedModel<TransactionResponse> searchTransactions(
	        String userId,
	        String categoryId,
	        String budgetId,
	        Double minAmount,
	        Double maxAmount,
	        LocalDate startDate,
	        LocalDate endDate,
	        String description,
	        int page,
	        int size,
	        String sortBy,
	        String direction);
}