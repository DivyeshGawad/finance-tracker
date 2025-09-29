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

import com.demo.finance_tracker_backend.entity.BudgetEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BudgetCustomRepository {

	private final MongoTemplate mongoTemplate;
	
	public Page<BudgetEntity> searchBudgets(
			String userId,
			String categoryId,
			Double minAmount,
			Double maxAmount,
			LocalDate startDate,
			LocalDate endDate,
			String note,
			Pageable pageable
			){
		List<Criteria> criteriaList = new ArrayList<>();
		
		criteriaList.add(Criteria.where("userId").is(userId));
		
		if(categoryId != null && !categoryId.isBlank()) {
			criteriaList.add(Criteria.where("categoryId").is(categoryId));
		}
		if(minAmount != null) {
		        criteriaList.add(Criteria.where("budgetAmount").gte(minAmount));
		}
		if(maxAmount != null) {
		        criteriaList.add(Criteria.where("budgetAmount").lte(maxAmount));
		}
		if(startDate != null) {
			criteriaList.add(Criteria.where("startDate").gte(startDate));
		}
		if(endDate != null) {
			criteriaList.add(Criteria.where("endDate").lte(endDate));
		}
		if (note != null && !note.isBlank()) {
            criteriaList.add(Criteria.where("note")
                    .regex(".*" + Pattern.quote(note) + ".*", "i"));
        }
		Criteria finalCriteriaList = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
		Query query = new Query(finalCriteriaList).with(pageable);
		
		List<BudgetEntity> results = mongoTemplate.find(query, BudgetEntity.class);
		long count = mongoTemplate.count((Query.query(finalCriteriaList)), BudgetEntity.class);
		
		return new PageImpl<>(results, pageable, count);
	}
}
