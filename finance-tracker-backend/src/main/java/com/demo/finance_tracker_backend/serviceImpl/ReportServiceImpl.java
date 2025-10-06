package com.demo.finance_tracker_backend.serviceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.demo.finance_tracker_backend.dto.ReportBudgetResponse;
import com.demo.finance_tracker_backend.dto.ReportCategoryResponse;
import com.demo.finance_tracker_backend.dto.ReportSummaryResponse;
import com.demo.finance_tracker_backend.dto.ReportTrendResponse;
import com.demo.finance_tracker_backend.entity.BudgetEntity;
import com.demo.finance_tracker_backend.entity.CategoryEntity;
import com.demo.finance_tracker_backend.entity.TransactionEntity;
import com.demo.finance_tracker_backend.enums.CategoryType;
import com.demo.finance_tracker_backend.exception.ResourceNotFoundException;
import com.demo.finance_tracker_backend.repository.BudgetRepository;
import com.demo.finance_tracker_backend.repository.CategoryRepository;
import com.demo.finance_tracker_backend.repository.TransactionRepository;
import com.demo.finance_tracker_backend.service.ReportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

	private final TransactionRepository transactionRepository;
	private final CategoryRepository categoryRepository;
	private final BudgetRepository budgetRepository;
	
	 /**
     * SUMMARY REPORT
     * Total Income, Expense, Balance in given date range
     */
	@Override
	public ReportSummaryResponse getSummaryReport(String userId, LocalDate startDate, LocalDate endDate) {
		log.info("Generating Summary Report for userId={}, startDate={}, endDate={}",userId, startDate, endDate);
		
		List<TransactionEntity> transactions = transactionRepository
				.findByUserIdAndTransactionDateBetween(userId, startDate, endDate.plusDays(1));
		
		if(transactions.isEmpty()) {
			log.warn("No Transaction found for Summary Report, userId={}, startDate={}, endDate={}",userId, startDate, endDate);
			
			throw new ResourceNotFoundException("No Transaction found for Summary Report");
		}
		
		double income = transactions.stream()
				.filter(t -> isCategoryType(t.getCategoryId(), CategoryType.INCOME))
				.mapToDouble(TransactionEntity::getAmount).sum();
		
		double expense = transactions.stream()
				.filter(t -> isCategoryType(t.getCategoryId(), CategoryType.EXPENSE))
				.mapToDouble(TransactionEntity::getAmount).sum();
		
		double balance = income - expense;
		
        log.info("Summary Report for user {}: Income={}, Expense={}, Balance={}", userId, income, expense, balance);

        return new ReportSummaryResponse(income, expense, balance);
	}

	/**
     * CATEGORY REPORT
     * Expense grouped by category
     */
	@Override
	public List<ReportCategoryResponse> getCategoryReport(String userId, LocalDate startDate, LocalDate endDate) {
		
		List<TransactionEntity> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate.plusDays(1));
		
		// Group expenses By categoryId
		Map<String, Double> expenseMap = transactions.stream()
				.filter(t -> isCategoryType(t.getCategoryId(), CategoryType.EXPENSE))
				.collect(Collectors.groupingBy(
						TransactionEntity::getCategoryId,
						Collectors.summingDouble(TransactionEntity::getAmount)
						));
		
		// FIXED: remove filter(EXPENSE only), now group all
//        Map<String, Double> groupedMap = transactions.stream()
//                .collect(Collectors.groupingBy(
//                        TransactionEntity::getCategoryId,
//                        Collectors.summingDouble(TransactionEntity::getAmount)));
		
		List<ReportCategoryResponse> response = new ArrayList<>();
		for(Map.Entry<String, Double> entry : expenseMap.entrySet()) {
			String categoryId = entry.getKey();
			double total = entry.getValue();
			
			CategoryEntity category = categoryRepository.findByCategoryId(categoryId)
					.orElseThrow(() -> new ResourceNotFoundException("Category not found for ID " + categoryId));
			
			response.add(new ReportCategoryResponse(category.getCategoryId() ,category.getName(), category.getType(), total));
		}
        log.info("Category Report for user {}: {}", userId, response);
        return response;
	}

	 /**
     * BUDGET REPORT
     * For each active budget → total spent + remaining
     */
	@Override
    public List<ReportBudgetResponse> getBudgetReport(String userId, LocalDate startDate, LocalDate endDate) {
        
		// Fetch all budgets of user
        List<BudgetEntity> allBudgetbudgets = budgetRepository.findByUserId(userId, null).getContent(); // if Pageable not required

        // Fetch all categories for the userId
        List<CategoryEntity> categories = new ArrayList<>();
        
        // Load system default categories
        categories.addAll(categoryRepository.findByIsDefaultTrue());
        
        // Load user's own categories
        categories.addAll(categoryRepository.findByUserId(userId));
        Map<String, String> categoryMap = categories.stream()
        		.collect(Collectors.toMap(CategoryEntity::getCategoryId, CategoryEntity::getName));
        
        // filter only budgets active in [startDate, endDate]
        List<BudgetEntity> budgets = allBudgetbudgets.stream()
        		.filter(b -> !(b.getEndDate().isBefore(startDate) || b.getStartDate().isAfter(endDate)))
        		.toList();
        
        List<ReportBudgetResponse> response = new ArrayList<>();

        for (BudgetEntity budget : budgets) {
        	
        	LocalDate effectiveStrat = budget.getStartDate().isBefore(startDate) ? startDate : budget.getStartDate();
        	LocalDate effectiveEnd = budget.getEndDate().isAfter(endDate) ? endDate : budget.getEndDate();
        	
        	// Fetch expenses inside this budget’s date range
            List<TransactionEntity> expenses = transactionRepository.findByUserIdAndCategoryIdAndTransactionDateBetween(
                    userId,
                    budget.getCategoryId(),
                    effectiveStrat,
                    effectiveEnd
            );

            double spent = expenses.stream().mapToDouble(TransactionEntity::getAmount).sum();
            double remaining = budget.getBudgetAmount() - spent;
            double totalBudget = budget.getBudgetAmount();
            double utilization = (totalBudget > 0) ? (spent / totalBudget) * 100 : 0.0;
            String categoryName = categoryMap.getOrDefault(budget.getCategoryId(), "UNKNOWN");

            response.add(new ReportBudgetResponse(
                    budget.getBudgetId(),
                    budget.getName(),
                    budget.getCategoryId(),
                    categoryName,
                    spent,
                    remaining,
                    budget.getBudgetAmount(),
                    budget.getStartDate(),
                    budget.getEndDate(),
                    utilization
            ));
        }

        log.info("Budget Report for user {}: {}", userId, response);
        return response;
    }

	/**
     * TREND REPORT
     * Shows income vs expense trend month by month
     */
	 @Override
	    public List<ReportTrendResponse> getTrendReport(String userId, LocalDate startDate, LocalDate endDate) {
	        List<TransactionEntity> transactions = transactionRepository
	                .findByUserIdAndTransactionDateBetween(userId, startDate, endDate);

	        // Group by Year-Month
	        Map<String, List<TransactionEntity>> byMonth = transactions.stream()
	                .collect(Collectors.groupingBy(
	                        t -> t.getTransactionDate().getYear() + "-" + String.format("%02d", t.getTransactionDate().getMonthValue())
	                ));

	        List<ReportTrendResponse> trendList = new ArrayList<>();

	        for (Map.Entry<String, List<TransactionEntity>> entry : byMonth.entrySet()) {
	            String month = entry.getKey();
	            List<TransactionEntity> txns = entry.getValue();

	            double income = txns.stream()
	                    .filter(t -> isCategoryType(t.getCategoryId(), CategoryType.INCOME))
	                    .mapToDouble(TransactionEntity::getAmount).sum();

	            double expense = txns.stream()
	                    .filter(t -> isCategoryType(t.getCategoryId(), CategoryType.EXPENSE))
	                    .mapToDouble(TransactionEntity::getAmount).sum();

	            trendList.add(new ReportTrendResponse(month, income, expense));
	        }

	        // Sort by month (ascending)
	        trendList.sort(Comparator.comparing(ReportTrendResponse::getMonth));

	        log.info("Trend Report for user {}: {}", userId, trendList);
	        return trendList;
	    }
	 
	 @Override
	 public List<ReportCategoryResponse> getTopCategoriesReport(String userId, LocalDate startDate, LocalDate endDate,
			 CategoryType type, int topN) {
		 log.info("Generating Top Categories Report for userId={}, startDate={}, endDate={}, type={}, topN={}", 
	             userId, startDate, endDate, type, topN);
		 
		 // Fetch All Transaction for the user in the date range
		 List<TransactionEntity> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
		 
		 if(transactions.isEmpty()) {
		        log.warn("No transactions found for Top Categories report for userId={}, startDate={}, endDate={}", 
		                 userId, startDate, endDate);
		        throw new ResourceNotFoundException("No transactions found for Top Categories report");
		    }

		 // Filter by type (if provided)
		 if(type != null) {
			 transactions = transactions.stream()
					 .filter(t -> isCategoryType(t.getCategoryId(), type))
					 .collect(Collectors.toList());
		 }
		 
		 // Group by categoryId and sum amounts
		 Map<String, Double> categoryTotals = transactions.stream()
				 .collect(Collectors.groupingBy(
						 TransactionEntity::getCategoryId,
						 Collectors.summingDouble(TransactionEntity::getAmount)
						 ));
		 
		 // Sort by total descending and take top N
		 List<Map.Entry<String, Double>> sortedTop = categoryTotals.entrySet().stream()
				 .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
				 .limit(topN)
				 .toList();
		 
		 // Convert to DTO list
		 List<ReportCategoryResponse> response = new ArrayList<>();
		 int rank = 1;
		 for (Map.Entry<String, Double> entry: sortedTop) {
			 String categoryId = entry.getKey();
			 double total = entry.getValue();
			 
			 CategoryEntity category = categoryRepository.findByCategoryId(categoryId)
		                .orElseThrow(() -> new ResourceNotFoundException("Category not found for ID " + categoryId));
			 
			 ReportCategoryResponse report = new ReportCategoryResponse(
					 category.getCategoryId(),
					 category.getName(),
					 category.getType(),
					 total
					 );
			 
			 report.setRank(rank++);
			 response.add(report);
		 }
		 
		 log.info("Top Categories Report for user {}: {}", userId, response);
		    return response;
	 }

	 /**
     * Helper: check category type safely
     */
    private boolean isCategoryType(String categoryId, CategoryType type) {
        return categoryRepository.findByCategoryId(categoryId)
                .map(c -> c.getType() == type)
                .orElse(false);
    }


}
