package com.demo.finance_tracker_backend.service;

import java.time.LocalDate;

public interface ReportExportService {

	 /**
     * Generate CSV stream for Summary Report
     */
	byte[] exportAsCsv(String userId, LocalDate startDate, LocalDate endDate);
	
	/**
     * Generate Excel stream for Summary Report
     */
	byte[] exportAsExcel(String userId, LocalDate startDate, LocalDate endDate);
	
	/**
     * Generate PDF stream for Summary Report
     */
	byte[] exportAsPdf(String userId, LocalDate startDate, LocalDate endDate);
}
