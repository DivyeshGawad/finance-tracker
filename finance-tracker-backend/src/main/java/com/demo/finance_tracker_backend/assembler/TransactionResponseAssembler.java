package com.demo.finance_tracker_backend.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.Optional;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.demo.finance_tracker_backend.controller.TransactionController;
import com.demo.finance_tracker_backend.dto.TransactionResponse;
import com.demo.finance_tracker_backend.entity.CategoryEntity;
import com.demo.finance_tracker_backend.entity.TransactionEntity;
import com.demo.finance_tracker_backend.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionResponseAssembler implements RepresentationModelAssembler<TransactionEntity, TransactionResponse> {

	
	private final CategoryRepository categoryRepository;
	
    @Override
    public TransactionResponse toModel(TransactionEntity entity) {

    	// Fetch category info safely using Optional
        Optional<CategoryEntity> categoryOpt = Optional.ofNullable(entity.getCategoryId())
                                                      .flatMap(categoryRepository::findByCategoryId);

        String categoryName = categoryOpt.map(CategoryEntity::getName).orElse(null);
        String categoryType = categoryOpt.map(c -> c.getType().name()).orElse(null);
        
        TransactionResponse response = TransactionResponse.builder()
                .transactionId(entity.getTransactionId())
                .userId(entity.getUserId())
                .categoryId(entity.getCategoryId())
                .budgetId(entity.getBudgetId())
                .categoryName(categoryName)
                .categoryType(categoryType)
                .description(entity.getDescription())
                .amount(entity.getAmount())
                .transactionDate(entity.getTransactionDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

        // Self link
        response.add(linkTo(methodOn(TransactionController.class)
                .getTransactionById(entity.getTransactionId())).withSelfRel().withType("GET"));

        // Update + Delete links only for user-owned transactions
        if (entity.getUserId() != null) {
            response.add(linkTo(methodOn(TransactionController.class)
                    .updateTransaction(entity.getTransactionId(), null)).withRel("update").withType("PUT"));
            response.add(linkTo(methodOn(TransactionController.class)
                    .deleteTransaction(entity.getTransactionId())).withRel("delete").withType("DELETE"));
        }

        return response;
    }
}
