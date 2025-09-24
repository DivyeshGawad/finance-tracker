package com.demo.finance_tracker_backend.serviceImpl;

import java.time.Instant;
import java.time.LocalDate;
import java.util.regex.Pattern;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.finance_tracker_backend.assembler.TransactionResponseAssembler;
import com.demo.finance_tracker_backend.dto.TransactionRequest;
import com.demo.finance_tracker_backend.dto.TransactionResponse;
import com.demo.finance_tracker_backend.entity.TransactionEntity;
import com.demo.finance_tracker_backend.exception.ResourceNotFoundException;
import com.demo.finance_tracker_backend.exception.UnauthorizedException;
import com.demo.finance_tracker_backend.repository.CategoryRepository;
import com.demo.finance_tracker_backend.repository.TransactionCustomRepository;
import com.demo.finance_tracker_backend.repository.TransactionRepository;
import com.demo.finance_tracker_backend.service.TransactionService;
import com.demo.finance_tracker_backend.util.IdGeneratorUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

	private final TransactionRepository transactionRepository;
	private final TransactionCustomRepository transactionCustomRepository;
	private final CategoryRepository categoryRepository;
	private final TransactionResponseAssembler assembler;
	private final PagedResourcesAssembler<TransactionEntity> pagedResourcesAssembler;
	
	/*
	 * Create a new Transaction for user
	 * */
	@Override
	@Transactional
	public TransactionResponse createTransaction(String userId, TransactionRequest request) {
		
		if (request.getCategoryId() != null) {
		    categoryRepository.findByCategoryId(request.getCategoryId())
		        .orElseThrow(() -> new ResourceNotFoundException("Invalid category for transaction"));
		}

		Instant now = Instant.now();
		
		TransactionEntity txn = TransactionEntity.builder()
				.transactionId(IdGeneratorUtil.generatePrefixedId("TXN"))
				.userId(userId)
				.categoryId(request.getCategoryId())
				.description(request.getDescription())
				.amount(request.getAmount())
				.transactionDate(request.getTransactionDate())
				.createdAt(now)
				.updatedAt(now)
				.build();
		
		try {
			transactionRepository.save(txn);
		} catch (DuplicateKeyException e) {
			log.error("Duplicate Transaction id conflict ", e);
			throw new DuplicateKeyException("Transaction Id Conflict, Try again",e);
		}
		
		log.info("Created Transaction {] for user {}", txn.getTransactionId(), userId);
		
		return assembler.toModel(txn);
	}

	/*
	 * Update a existing Transaction for a user
	 * */
	@Override
	@Transactional
	public TransactionResponse updateTransaction(String transactionId, TransactionRequest request, String userId) {
		TransactionEntity txn = transactionRepository.findByTransactionId(transactionId)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction Not Found"));
		
		if(!txn.getUserId().equals(userId)) {
			throw new UnauthorizedException("You cannot update this transaction");
		}
		
		if (request.getCategoryId() != null) {
		    categoryRepository.findByCategoryId(request.getCategoryId())
		        .orElseThrow(() -> new ResourceNotFoundException("Invalid category for transaction"));
		}
		
		txn.setCategoryId(request.getCategoryId());
		txn.setDescription(request.getDescription());
		txn.setAmount(request.getAmount());
		txn.setTransactionDate(request.getTransactionDate());
		txn.setUpdatedAt(Instant.now());
		
		transactionRepository.save(txn);
		log.info("Updated Transaction {} by user {}", transactionId, userId);
		
		return assembler.toModel(txn);
	}

	/*
	 * Delete a Transaction for user
	 * */
	@Transactional
	@Override
	public void deleteTransaction(String transactionId, String userId) {
		TransactionEntity txn = transactionRepository.findByTransactionId(transactionId)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction Not Found"));
		
		if(!txn.getUserId().equals(userId)) {
			throw new UnauthorizedException("You are not allow to delete this transaction");
		}
		
		transactionRepository.deleteByTransactionId(transactionId);
		log.info("Delete Transaction {} by user {}", transactionId, userId);

	}

	/*
	 * Get Transaction By ID
	 * */
	@Override
	public TransactionResponse getTransactionById(String transactionId, String userId) {
		TransactionEntity txn = transactionRepository.findByTransactionId(transactionId)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction Not Found"));
		
		if(!txn.getUserId().equals(userId)) {
			throw new UnauthorizedException("You are not allowed to view this transaction");
		}
		
		return assembler.toModel(txn);
	}

	/*
	 * Get All Transaction for user
	 * */
	@Override
	public PagedModel<TransactionResponse> getUserTransactionsPaged(String userId, int page, int size, String sortBy,
			String direction) {
		
		Sort sort = "asc".equalsIgnoreCase(direction)
				? Sort.by(sortBy).ascending().and(Sort.by("transactionId").ascending())
				: Sort.by(sortBy).descending().and(Sort.by("transactionId").descending());
		
		Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
		
		Page<TransactionEntity> pageResult = transactionRepository.findByUserId(userId, pageable);
		
		PagedModel<TransactionResponse> model = pagedResourcesAssembler.toModel(pageResult,assembler);
		
		return model;
	}

	/* Search Transactions */
	@Override
	public PagedModel<TransactionResponse> searchTransactions(
	        String userId,
	        String categoryId,
	        Double minAmount,
	        Double maxAmount,
	        LocalDate startDate,
	        LocalDate endDate,
	        String description,
	        int page,
	        int size,
	        String sortBy,
	        String direction) {

	    // Build safe regex for description
	    String safeDescription = (description == null || description.isBlank()) 
	            ? null // let repository ignore description filter
	            : "(?i).*" + Pattern.quote(description.trim()) + ".*";

	    // Deterministic sorting (primary + secondary)
	    Sort sort = "asc".equalsIgnoreCase(direction)
	            ? Sort.by(sortBy).ascending().and(Sort.by("transactionId").ascending())
	            : Sort.by(sortBy).descending().and(Sort.by("transactionId").descending());

	    Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);

	    // ✅ use custom repository
	    Page<TransactionEntity> pageResult = transactionCustomRepository.searchTransactions(
	            userId,
	            categoryId,
	            minAmount,
	            maxAmount,
	            startDate,
	            endDate,
	            safeDescription,
	            pageable
	    );
	    
	    log.info("Searching...."+pageResult);
	    return pagedResourcesAssembler.toModel(pageResult, assembler);
	}


	/*
	 * Validate user ownership
	 * */
	@Override
	public boolean isTransactionValidForUser(String transactionId, String userId) {
		return transactionRepository.findByTransactionId(transactionId)
				.filter(e -> e.getUserId().equals(userId))
				.isPresent();
	}

}