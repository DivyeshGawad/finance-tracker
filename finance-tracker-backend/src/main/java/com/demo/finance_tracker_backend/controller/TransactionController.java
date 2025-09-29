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
import com.demo.finance_tracker_backend.dto.TransactionRequest;
import com.demo.finance_tracker_backend.dto.TransactionResponse;
import com.demo.finance_tracker_backend.security.CurrentUser;
import com.demo.finance_tracker_backend.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

	private final TransactionService transactionService;
	
	private final CurrentUser currentUser;
	
	/* Create a new Transaction */
	@PostMapping
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(@Valid @RequestBody TransactionRequest request){
		String userId = currentUser.getUserId();
		
		TransactionResponse response = transactionService.createTransaction(userId, request);
		
		return ResponseEntity.ok(ApiResponse.success("Transaction Created Successfully", response));
	}
	
	/* Update existing Transaction */
	@PutMapping("/{transactionId}")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<ApiResponse<TransactionResponse>> updateTransaction(
			@PathVariable String transactionId,
			@Valid @RequestBody TransactionRequest request
			){
		
		String userId = currentUser.getUserId();
		
		TransactionResponse response = transactionService.updateTransaction(transactionId, request, userId);
		
		return ResponseEntity.ok(ApiResponse.success("Transaction updated Successfully", response));
	}
	
	 /** Delete a transaction */
	@DeleteMapping("/{transactionId}")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable String transactionId){
		
		String userId = currentUser.getUserId();
		
		transactionService.deleteTransaction(transactionId, userId);
		
		return ResponseEntity.ok(ApiResponse.success("Transaction Deleted Successfully."));
	}
	
	/* Get Transaction By ID */
	@GetMapping("/{transactionId}")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(@PathVariable String transactionId){
		String userId = currentUser.getUserId();
		
		TransactionResponse response = transactionService.getTransactionById(transactionId, userId);
		
		return ResponseEntity.ok(ApiResponse.success("Transaction fetched successfully for ID "+transactionId, response));	
	}
	
	/* Get All Transaction for user (paged + HATEOAS) */
	@GetMapping("/myTransactions")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<ApiResponse<PagedModel<TransactionResponse>>> getTransactionsPaged(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "transactionDate") String sortBy,
			@RequestParam(defaultValue = "desc") String direction
			) {
		String userId = currentUser.getUserId();
		PagedModel<TransactionResponse> pagedModel = transactionService.getUserTransactionsPaged(userId, page, size, sortBy, direction);
		
		return ResponseEntity.ok(ApiResponse.success("Successfully fetched all transaction for user "+userId, pagedModel));
	}
	
	@GetMapping("/search")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public ResponseEntity<ApiResponse<PagedModel<TransactionResponse>>> searchTransactions(
	        @RequestParam(required = false) String categoryId,
	        @RequestParam(required = false) String budgetId,
	        @RequestParam(required = false) Double minAmount,
	        @RequestParam(required = false) Double maxAmount,
	        @RequestParam(required = false) LocalDate startDate,
	        @RequestParam(required = false) LocalDate endDate,
	        @RequestParam(required = false) String description,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "transactionDate") String sortBy,
	        @RequestParam(defaultValue = "desc") String direction
	) {
	    String userId = currentUser.getUserId();
	    PagedModel<TransactionResponse> pagedModel = transactionService.searchTransactions(
	            userId,
	            categoryId,
		        budgetId,
	            minAmount,
	            maxAmount,
	            startDate,
	            endDate,
	            description,
	            page,
	            size,
	            sortBy,
	            direction
	    );

	    if (pagedModel.getMetadata().getTotalElements() == 0) {
	        return ResponseEntity.ok(ApiResponse.error("Transaction Not Found.", null));
	    }

	    return ResponseEntity.ok(ApiResponse.success("Transactions fetched successfully", pagedModel));
	}

}