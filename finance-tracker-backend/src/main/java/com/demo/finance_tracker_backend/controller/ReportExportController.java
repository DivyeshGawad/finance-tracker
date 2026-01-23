package com.demo.finance_tracker_backend.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.demo.finance_tracker_backend.security.CurrentUser;
import com.demo.finance_tracker_backend.service.ReportExportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports/export")
@CrossOrigin(origins = "*")
public class ReportExportController {
	
	private final ReportExportService exportService;
	private final CurrentUser currentUser;
	
	@GetMapping("/csv")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<byte[]> exportCsv(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
			){
		String userId = currentUser.getUserId();
		byte[] csvData = exportService.exportAsCsv(userId, startDate, endDate);
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = transactions.csv")
				.contentType(MediaType.parseMediaType("text/csv"))
				.body(csvData);
	}
	
	@GetMapping("/excel")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<byte []> exportExcel(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
			){
		String userId = currentUser.getUserId();
		
		byte [] excelFile = exportService.exportAsExcel(userId, startDate, endDate);
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = transactions.xlsx")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(excelFile);
	}
	
	@GetMapping("/pdf")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<byte []> exportPdf(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
			){
		String userId = currentUser.getUserId();
		
		byte [] pdfFile = exportService.exportAsPdf(userId, startDate, endDate);
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = transactions.pdf")
				.body(pdfFile);
	}
}
