package com.demo.finance_tracker_backend.serviceImpl;

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

import com.demo.finance_tracker_backend.assembler.BudgetResponseAssembler;
import com.demo.finance_tracker_backend.dto.BudgetRequest;
import com.demo.finance_tracker_backend.dto.BudgetResponse;
import com.demo.finance_tracker_backend.entity.BudgetEntity;
import com.demo.finance_tracker_backend.entity.CategoryEntity;
import com.demo.finance_tracker_backend.exception.ResourceNotFoundException;
import com.demo.finance_tracker_backend.exception.UnauthorizedException;
import com.demo.finance_tracker_backend.repository.BudgetCustomRepository;
import com.demo.finance_tracker_backend.repository.BudgetRepository;
import com.demo.finance_tracker_backend.repository.CategoryRepository;
import com.demo.finance_tracker_backend.service.BudgetService;
import com.demo.finance_tracker_backend.util.IdGeneratorUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetServiceImpl implements BudgetService {

	private final BudgetRepository budgetRepositpry;
	private final BudgetCustomRepository budgetCustomRepository;
	private final CategoryRepository categoryRepository;
	private final BudgetResponseAssembler assembler;
	private final PagedResourcesAssembler<BudgetEntity> pagedResourcesAssembler;

	@Override
	@Transactional
	public BudgetResponse createBudget(String userId, BudgetRequest request) {
		if (request.getCategoryId() != null) {
			CategoryEntity category =  categoryRepository.findByCategoryId(request.getCategoryId())
					.orElseThrow(() -> new ResourceNotFoundException("Invalid Caetgory for Budget"));
			
	        // Prevent budget creation for INCOME categories
			if("INCOME".equalsIgnoreCase(category.getType().toString())) {
				throw new IllegalArgumentException("Budgets can only be assigned to 'EXPENSE' Categories");
			}
		}
		
		BudgetEntity budget = BudgetEntity.builder().budgetId(IdGeneratorUtil.generatePrefixedId("BUD")).userId(userId)
				.categoryId(request.getCategoryId()).budgetAmount(request.getBudgetAmount()).spendAmount(0.0)
				.startDate(request.getStartDate()).endDate(request.getEndDate()).note(request.getNote()).build();

		try {
			budgetRepositpry.save(budget);
		} catch (DuplicateKeyException e) {
			log.error("Duplicate Budget ID conflict.", e);
			throw new DuplicateKeyException("Duplicate Budget ID conflict, Try Again", e);
		}
		log.info("Creared Budget {} successfully for user {}", budget.getBudgetId(), userId);

		return assembler.toModel(budget);
	}

	@Override
	@Transactional
	public BudgetResponse updateBudget(String budgetId, String userId, BudgetRequest request) {
		BudgetEntity budget = budgetRepositpry.findByBudgetId(budgetId)
				.orElseThrow(() -> new ResourceNotFoundException("Budget Not Found for Update" + budgetId));

		if (!budget.getUserId().equals(userId)) {
			throw new UnauthorizedException("You cannot update this budget");
		}

		if (request.getCategoryId() != null) {
			CategoryEntity category = categoryRepository.findByCategoryId(request.getCategoryId())
					.orElseThrow(() -> new ResourceNotFoundException("Invalid Caetgory for Budget"));
			
			// Prevent budget creation for INCOME categories
						if("INCOME".equalsIgnoreCase(category.getType().toString())) {
							throw new IllegalArgumentException("Budgets can only be assigned to 'EXPENSE' Categories");
						}
		}

		budget.setCategoryId(request.getCategoryId());
		budget.setBudgetAmount(request.getBudgetAmount());
		budget.setStartDate(request.getStartDate());
		budget.setEndDate(request.getEndDate());
		budget.setNote(request.getNote());

		budgetRepositpry.save(budget);
		log.info("Updated Budget {} successfully for user {}", budgetId, userId);

		return assembler.toModel(budget);

	}

	@Override
	@Transactional
	public void deleteBudget(String budgetId, String userId) {

		BudgetEntity budget = budgetRepositpry.findByBudgetId(budgetId)
				.orElseThrow(() -> new ResourceNotFoundException("Budget Not Found"));

		if (!budget.getUserId().equals(userId)) {
			throw new UnauthorizedException("You cannot delete this budget");
		}

		budgetRepositpry.deleteByBudgetId(budgetId);
		log.info("Deleted Budget {} successfully for user {}", budgetId, userId);

	}

	@Override
	public BudgetResponse getBudgetById(String budgetId, String userId) {
		BudgetEntity budget = budgetRepositpry.findByBudgetId(budgetId)
				.orElseThrow(() -> new ResourceNotFoundException("Budget Not Found"));

		if (!budget.getUserId().equals(userId)) {
			throw new UnauthorizedException("You cannot view this budget");
		}

		return assembler.toModel(budget);
	}

	// ✅ Get All Budgets Paged
	@Override
	public PagedModel<BudgetResponse> getUserBudgetsPaged(String userId, int page, int size, String sortBy,
			String direction) {

		Sort sort = "asc".equalsIgnoreCase(direction) ? Sort.by(sortBy).ascending().and(Sort.by("budgetId").ascending())
				: Sort.by(sortBy).descending().and(Sort.by("budgetId").descending());

		Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
		Page<BudgetEntity> pageResult = budgetRepositpry.findByUserId(userId, pageable);

		return pagedResourcesAssembler.toModel(pageResult, assembler);
	}

	@Override
	public PagedModel<BudgetResponse> searchBudgets(String userId,
            String categoryId,
            Double minBudgetAmount,
            Double maxBudgetAmount,
            LocalDate startDate,
            LocalDate endDate,
            String noteKeyword,
            int page,
            int size,
            String sortBy,
            String direction) {

		// 🔧 FIX: Safe regex for note keyword
		String safeNoteKeyword = (noteKeyword == null || noteKeyword.isBlank()) ? null
				: "(?i).*" + Pattern.quote(noteKeyword.trim()) + ".*";

		Sort sort = "asc".equalsIgnoreCase(direction) ? Sort.by(sortBy).ascending().and(Sort.by("budgetId").ascending())
				: Sort.by(sortBy).descending().and(Sort.by("budgetId").descending());

		Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);

		Page<BudgetEntity> pageResult = budgetCustomRepository.searchBudgets(userId, categoryId, minBudgetAmount,
				maxBudgetAmount, startDate, endDate, safeNoteKeyword, pageable);

		return pagedResourcesAssembler.toModel(pageResult, assembler);
	}

	@Override
	@Transactional
	public void adjustBudgetForCategoryChange(
	        String userId,
	        String oldBudgetId,
	        String newBudgetId,
	        Double oldAmount,
	        Double newAmount,
	        String oldCategoryType,
	        String newCategoryType
	) {

	    // 🔒 Defensive: if neither old nor new is EXPENSE, skip completely
	    if (!"EXPENSE".equalsIgnoreCase(oldCategoryType) && 
	        !"EXPENSE".equalsIgnoreCase(newCategoryType)) {
	        log.debug("No budget adjustment needed (not an EXPENSE transaction)");
	        return;
	    }

	    // 1️ Reduce old budget if old category was EXPENSE
	    if ("EXPENSE".equalsIgnoreCase(oldCategoryType) && oldBudgetId != null) {
	        budgetRepositpry.findByBudgetId(oldBudgetId).ifPresent(budget -> {
	            double spend = budget.getSpendAmount() != null ? budget.getSpendAmount() : 0.0;
	            spend -= oldAmount != null ? oldAmount : 0.0;
	            if (spend < 0) spend = 0.0; // don’t let it go negative
	            budget.setSpendAmount(spend);
	            budgetRepositpry.save(budget);
	            log.info("Adjusted old Budget {} spendAmount to {}", budget.getBudgetId(), spend);
	        });
	    }

	    // 2️ Add to new budget if new category is EXPENSE
	    if ("EXPENSE".equalsIgnoreCase(newCategoryType) && newBudgetId != null) {
	        budgetRepositpry.findByBudgetId(newBudgetId).ifPresent(budget -> {
	            double spend = budget.getSpendAmount() != null ? budget.getSpendAmount() : 0.0;
	            spend += newAmount != null ? newAmount : 0.0;
	            budget.setSpendAmount(spend);
	            budgetRepositpry.save(budget);
	            log.info("Updated new Budget {} spendAmount to {}", budget.getBudgetId(), spend);
	        });
	    }
	}


}
