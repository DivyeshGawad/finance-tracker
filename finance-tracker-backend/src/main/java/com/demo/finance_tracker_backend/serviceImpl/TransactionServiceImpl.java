package com.demo.finance_tracker_backend.serviceImpl;

import java.time.LocalDate;
import java.util.regex.Pattern;

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
import com.demo.finance_tracker_backend.entity.CategoryEntity;
import com.demo.finance_tracker_backend.entity.TransactionEntity;
import com.demo.finance_tracker_backend.exception.ResourceNotFoundException;
import com.demo.finance_tracker_backend.exception.UnauthorizedException;
import com.demo.finance_tracker_backend.repository.BudgetRepository;
import com.demo.finance_tracker_backend.repository.CategoryRepository;
import com.demo.finance_tracker_backend.repository.TransactionCustomRepository;
import com.demo.finance_tracker_backend.repository.TransactionRepository;
import com.demo.finance_tracker_backend.service.BudgetService;
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
    private final BudgetRepository budgetRepository;
    private final BudgetService budgetService;
    private final TransactionResponseAssembler assembler;
    private final PagedResourcesAssembler<TransactionEntity> pagedResourcesAssembler;

    @Override
    @Transactional
    public TransactionResponse createTransaction(String userId, TransactionRequest request) {

        CategoryEntity category = categoryRepository.findByCategoryId(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid category for transaction"));

        String categoryType = category.getType().name();
        
        // INCOME transaction → no budget
        if ("INCOME".equalsIgnoreCase(categoryType)) {
            TransactionEntity txn = TransactionEntity.builder()
                    .transactionId(IdGeneratorUtil.generatePrefixedId("TXN"))
                    .userId(userId)
                    .categoryId(request.getCategoryId())
                    .description(request.getDescription())
                    .amount(request.getAmount())
                    .transactionDate(request.getTransactionDate())
                    .build();

            transactionRepository.save(txn);
            log.info("Created INCOME Transaction {} for user {}", txn.getTransactionId(), userId);
            return assembler.toModel(txn);
        }

        // EXPENSE transaction → budget required
        if (request.getBudgetId() == null) {
            throw new IllegalArgumentException("BudgetId is mandatory for EXPENSE transactions");
        }

        budgetRepository.findByBudgetId(request.getBudgetId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid budget for transaction"));

        TransactionEntity txn = TransactionEntity.builder()
                .transactionId(IdGeneratorUtil.generatePrefixedId("TXN"))
                .userId(userId)
                .categoryId(request.getCategoryId())
                .budgetId(request.getBudgetId())
                .description(request.getDescription())
                .amount(request.getAmount())
                .transactionDate(request.getTransactionDate())
                .build();

        transactionRepository.save(txn);
        log.info("Created EXPENSE Transaction {} for user {}", txn.getTransactionId(), userId);

        // Adjust budget spend
        budgetService.adjustBudgetForCategoryChange(userId, null, txn.getBudgetId(), 0.0, txn.getAmount(), null, categoryType);

        return assembler.toModel(txn);
    }


    @Override
    @Transactional
    public TransactionResponse updateTransaction(String transactionId, TransactionRequest request, String userId) {

    	// Fetch existing transaction
    	TransactionEntity txn = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction Not Found"));

        // Authorization check
        if (!txn.getUserId().equals(userId)) {
            throw new UnauthorizedException("You cannot update this transaction");
        }

        // ✅ Validate new category
        CategoryEntity newCategory = categoryRepository.findByCategoryId(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category Not Found"));
        
        String newCategoryType = newCategory.getType().name();
        
        // 🔒 Prevent assigning budget to INCOME
        if("INCOME".equalsIgnoreCase(newCategoryType) && request.getBudgetId() != null) {
            throw new IllegalArgumentException("INCOME transactions cannot have a budget assigned");
        }
        
        // If EXPENSE, validate budget exists
        if ("EXPENSE".equalsIgnoreCase(newCategoryType) && request.getBudgetId() != null) {
            budgetRepository.findByBudgetId(request.getBudgetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid budget for transaction"));
        }
        
        // Old values
        double oldAmount = txn.getAmount();
        String oldCategoryType = null;
        String oldBudgetId = txn.getBudgetId();

        if (txn.getCategoryId() != null) {
            oldCategoryType = categoryRepository.findByCategoryId(txn.getCategoryId())
                    .map(c -> c.getType().name()).orElse(null);
        }

        // ❌ Prevent changing from EXPENSE <-> INCOME
        if (oldCategoryType != null && !oldCategoryType.equalsIgnoreCase(newCategoryType)) {
            throw new IllegalArgumentException(
                    "You cannot change transaction type from " + oldCategoryType + " to " + newCategoryType
            );
        }

        // ✅ Safe update (same type only)
        txn.setCategoryId(request.getCategoryId());
        txn.setDescription(request.getDescription());
        txn.setAmount(request.getAmount());
        txn.setTransactionDate(request.getTransactionDate());

        // Only set budgetId for EXPENSE transactions
        if("EXPENSE".equalsIgnoreCase(newCategoryType)) {
        	txn.setBudgetId(request.getBudgetId());
        }
        
        transactionRepository.save(txn);

        // Adjust budgets (only needed if still EXPENSE)
        budgetService.adjustBudgetForCategoryChange(
                userId, oldBudgetId, txn.getBudgetId(),
                oldAmount, txn.getAmount(),
                oldCategoryType, newCategoryType
        );

        log.info("Updated Transaction {} by user {}", transactionId, userId);
        return assembler.toModel(txn);
    }

    @Override
    @Transactional
    public void deleteTransaction(String transactionId, String userId) {
      	TransactionEntity txn = transactionRepository.findByTransactionId(transactionId)
                  .orElseThrow(() -> new ResourceNotFoundException("Transaction Not Found"));

        if (!txn.getUserId().equals(userId)) {
              throw new UnauthorizedException("You are not allowed to delete this transaction");
          }

        // Save old values
        double oldAmount = txn.getAmount();
        String oldCategoryType = null;
        String oldBudgetId = txn.getBudgetId();
        
        if(txn.getCategoryId() != null) {
        	oldCategoryType = categoryRepository.findByCategoryId(txn.getCategoryId())
        			.map(c -> c.getType().name()).orElse(null);
        }

        transactionRepository.deleteByTransactionId(transactionId);
        
        // Adjust budget for delete
        budgetService.adjustBudgetForCategoryChange(userId, oldBudgetId, null, oldAmount, 0.0, oldCategoryType, null);
        
        log.info("Deleted Transaction {} by user {}", transactionId, userId);
    }

    @Override
    public TransactionResponse getTransactionById(String transactionId, String userId) {
        TransactionEntity txn = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction Not Found"));

        if (!txn.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not allowed to view this transaction");
        }

        return assembler.toModel(txn);
    }

    @Override
    public PagedModel<TransactionResponse> getUserTransactionsPaged(String userId, int page, int size, String sortBy, String direction) {
        Sort sort = "asc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).ascending().and(Sort.by("transactionId").ascending())
                : Sort.by(sortBy).descending().and(Sort.by("transactionId").descending());

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
        Page<TransactionEntity> pageResult = transactionRepository.findByUserId(userId, pageable);
        return pagedResourcesAssembler.toModel(pageResult, assembler);
    }

    @Override
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
            String direction) {

        String safeDescription = (description == null || description.isBlank())
                ? null
                : "(?i).*" + Pattern.quote(description.trim()) + ".*";

        Sort sort = "asc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).ascending().and(Sort.by("transactionId").ascending())
                : Sort.by(sortBy).descending().and(Sort.by("transactionId").descending());

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);

        Page<TransactionEntity> pageResult = transactionCustomRepository.searchTransactions(
                userId,
                categoryId,
                budgetId,
                minAmount,
                maxAmount,
                startDate,
                endDate,
                safeDescription,
                pageable
        );

        log.info("Searching transactions: {}", pageResult);
        return pagedResourcesAssembler.toModel(pageResult, assembler);
    }
}
