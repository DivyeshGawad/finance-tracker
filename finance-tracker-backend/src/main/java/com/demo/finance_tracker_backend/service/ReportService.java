package com.demo.finance_tracker_backend.service;

import java.time.LocalDate;
import java.util.List;

import com.demo.finance_tracker_backend.dto.ReportBudgetResponse;
import com.demo.finance_tracker_backend.dto.ReportCategoryResponse;
import com.demo.finance_tracker_backend.dto.ReportSummaryResponse;
import com.demo.finance_tracker_backend.dto.ReportTrendResponse;
import com.demo.finance_tracker_backend.enums.CategoryType;

public interface ReportService {

    // 1 Get summary report for a user (total income, total expense, remaining budget)
	ReportSummaryResponse getSummaryReport(String userId, LocalDate startDate, LocalDate endDate);
	
    // 2 Get category-wise report (spending per category)
	List<ReportCategoryResponse> getCategoryReport(String userId, LocalDate startDate, LocalDate endDate);
	
    // 3 Get budget-wise report (allocated, spent, remaining)
	List<ReportBudgetResponse> getBudgetReport(String userId, LocalDate startDate, LocalDate endDate);
	
    // 4 Get trend report (income vs expense over time)
	List<ReportTrendResponse> getTrendReport(String userId, LocalDate startDate, LocalDate endDate);
    // period = "DAILY", "WEEKLY", "MONTHLY"

	// 5 Getting Top 3 Category according to dates
	List<ReportCategoryResponse> getTopCategoriesReport(String userId, LocalDate startDate, LocalDate endDate, CategoryType type, int topN);

}
