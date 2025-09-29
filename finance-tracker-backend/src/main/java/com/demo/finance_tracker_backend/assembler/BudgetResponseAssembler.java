package com.demo.finance_tracker_backend.assembler;

import java.util.Optional;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.stereotype.Component;

import com.demo.finance_tracker_backend.controller.BudgetController;
import com.demo.finance_tracker_backend.controller.TransactionController;
import com.demo.finance_tracker_backend.dto.BudgetResponse;
import com.demo.finance_tracker_backend.entity.BudgetEntity;
import com.demo.finance_tracker_backend.entity.CategoryEntity;
import com.demo.finance_tracker_backend.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BudgetResponseAssembler implements RepresentationModelAssembler<BudgetEntity, BudgetResponse>{
	
	private final CategoryRepository categoryRepository;
	
	@Override
	public BudgetResponse toModel(BudgetEntity entity) {
		
		// Fetch category info
        Optional<CategoryEntity> categoryOpt = categoryRepository.findByCategoryId(entity.getCategoryId());
        String categoryName = categoryOpt.map(CategoryEntity::getName).orElse(null);
        String categoryType = categoryOpt.map(c -> c.getType().name()).orElse(null);
        
        // Derived Fields
        double budgetAmount = entity.getBudgetAmount()!= null ? entity.getBudgetAmount() : 0.0;
        double spendAmount = entity.getSpendAmount()!= null ? entity.getSpendAmount() : 0.0;
        double percentUsed = (budgetAmount > 0) 
                ? Math.round((spendAmount / budgetAmount) * 10000.0) / 100.0 
                : 0.0; // Rounded to 2 decimals
        String status;
        if (budgetAmount == 0) {
            status = "UNKNOWN";
        } else if (spendAmount > budgetAmount) {
            status = "EXCEEDED";
        } else if (percentUsed >= 80) {
            status = "NEARING_LIMIT";
        } else {
            status = "ON_TRACK";
        }
        
        BudgetResponse response = BudgetResponse.builder()
                .budgetId(entity.getBudgetId())
                .userId(entity.getUserId())
                .categoryId(entity.getCategoryId())
                .categoryName(categoryName)
                .categoryType(categoryType)
                .budgetAmount(entity.getBudgetAmount())
                .spendAmount(entity.getSpendAmount())
                .percentUsed(percentUsed)
                .status(status)
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

        // ✅ HATEOAS links
        response.add(linkTo(methodOn(BudgetController.class)
                .getBudgetByBudgetId(entity.getBudgetId()))
                .withSelfRel().withType("GET"));

        response.add(linkTo(methodOn(BudgetController.class)
                .updateBudget(entity.getBudgetId(), null))
                .withRel("update").withType("PUT"));

        response.add(linkTo(methodOn(BudgetController.class)
                .deleteBudget(entity.getBudgetId()))
                .withRel("delete").withType("DELETE"));

        // ✅ Link to transactions within budget period
        if (entity.getCategoryId() != null && entity.getStartDate() != null && entity.getEndDate() != null) {
            response.add(linkTo(methodOn(TransactionController.class)
                    .searchTransactions(
                            entity.getCategoryId(),
                            entity.getBudgetId(),
                            null, null,
                            entity.getStartDate(),
                            entity.getEndDate(),
                            null,
                            0, 10,
                            "transactionDate", "desc"
                    )).withRel("transactions").withType("GET"));
        }

        return response;
	}

}
