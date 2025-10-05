package com.demo.finance_tracker_backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.finance_tracker_backend.dto.ApiResponse;
import com.demo.finance_tracker_backend.dto.ReportBudgetResponse;
import com.demo.finance_tracker_backend.dto.ReportCategoryResponse;
import com.demo.finance_tracker_backend.dto.ReportSummaryResponse;
import com.demo.finance_tracker_backend.dto.ReportTrendResponse;
import com.demo.finance_tracker_backend.enums.CategoryType;
import com.demo.finance_tracker_backend.security.CurrentUser;
import com.demo.finance_tracker_backend.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;
	private final CurrentUser currentUser;
	
	/**
     * 📌 Get Income vs Expense Summary
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<ReportSummaryResponse>> getSummary(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        String userId = currentUser.getUserId();
        ReportSummaryResponse summary = reportService.getSummaryReport(userId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success("Summary report generated successfully", summary));
    }

    /**
     * 📌 Get Category-wise Spending/Income
     */
    @GetMapping("/category")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<List<ReportCategoryResponse>>> getCategoryReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        String userId = currentUser.getUserId();
        List<ReportCategoryResponse> report = reportService.getCategoryReport(userId, startDate, endDate);

        if (report.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("No category report found in the given period", null));
        }

        return ResponseEntity.ok(ApiResponse.success("Category report generated successfully", report));
    }

    /**
     * 📌 Get Budget vs Actual Spend
     */
    @GetMapping("/budget")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<List<ReportBudgetResponse>>> getBudgetReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        String userId = currentUser.getUserId();
        List<ReportBudgetResponse> report = reportService.getBudgetReport(userId, startDate, endDate);

        if (report.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("No budget report found in the given period", null));
        }

        return ResponseEntity.ok(ApiResponse.success("Budget report generated successfully", report));
    }

    /**
     * 📌 Get Monthly Trend (Income vs Expense)
     */
    @GetMapping("/trend")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<List<ReportTrendResponse>>> getTrendReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        String userId = currentUser.getUserId();
        List<ReportTrendResponse> report = reportService.getTrendReport(userId, startDate, endDate);

        if (report.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("No trend data available for the given range", null));
        }

        return ResponseEntity.ok(ApiResponse.success("Trend report generated successfully", report));
    }
    
    /**
     *  Top Categories
     * */
    @GetMapping("/top-categories")
    public ResponseEntity<ApiResponse<List<ReportCategoryResponse>>> getTopCategoriesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) CategoryType type,
            @RequestParam(defaultValue = "3") int topN) {
        
//        log.info("API Request: Top Categories Report for userId={}, startDate={}, endDate={}, type={}, topN={}",
//                 userId, startDate, endDate, type, topN);

    	String userId = currentUser.getUserId();
        List<ReportCategoryResponse> report = reportService.getTopCategoriesReport(userId, startDate, endDate, type, topN);

        return ResponseEntity.ok(ApiResponse.success("Top categories report generated successfully", report));
    }

}
