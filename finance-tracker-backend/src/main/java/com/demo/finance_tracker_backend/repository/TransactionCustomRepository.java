package com.demo.finance_tracker_backend.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.demo.finance_tracker_backend.entity.TransactionEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TransactionCustomRepository {

	private final MongoTemplate mongoTemplate;
	
	public Page<TransactionEntity> searchTransactions(
	        String userId,
	        String categoryId,
	        String budgetId,
	        Double minAmount,
	        Double maxAmount,
	        LocalDate startDate,
	        LocalDate endDate,
	        String description,
	        Pageable pageable
	){
	    List<Criteria> criteriaList = new ArrayList<>();
	    criteriaList.add(Criteria.where("userId").is(userId));

	    if(categoryId != null && !categoryId.isBlank()) {
	        criteriaList.add(Criteria.where("categoryId").is(categoryId));
	    }
	    if(budgetId != null && !budgetId.isBlank()) {
	        criteriaList.add(Criteria.where("budgetId").is(budgetId));
	    }
	    if(minAmount != null) {
	        criteriaList.add(Criteria.where("amount").gte(minAmount));
	    }
	    if(maxAmount != null) {
	        criteriaList.add(Criteria.where("amount").lte(maxAmount));
	    }
	    if(startDate != null) {
	        criteriaList.add(Criteria.where("transactionDate").gte(startDate));
	    }
	    if(endDate != null) {
	        criteriaList.add(Criteria.where("transactionDate").lte(endDate));
	    }
	    if(description != null && !description.isBlank()) {
	        criteriaList.add(Criteria.where("description").regex(Pattern.compile(description, Pattern.CASE_INSENSITIVE)));
	    }

	    Criteria finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
	    Query query = new Query(finalCriteria).with(pageable);

	    List<TransactionEntity> results = mongoTemplate.find(query, TransactionEntity.class);
	    long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), TransactionEntity.class);

	    return new PageImpl<>(results, pageable, count);
	}
}
