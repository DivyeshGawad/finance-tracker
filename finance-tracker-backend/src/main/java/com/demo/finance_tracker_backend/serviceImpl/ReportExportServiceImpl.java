package com.demo.finance_tracker_backend.serviceImpl;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.demo.finance_tracker_backend.entity.CategoryEntity;
import com.demo.finance_tracker_backend.entity.TransactionEntity;
import com.demo.finance_tracker_backend.enums.CategoryType;
import com.demo.finance_tracker_backend.repository.CategoryRepository;
import com.demo.finance_tracker_backend.repository.TransactionRepository;
import com.demo.finance_tracker_backend.service.ReportExportService;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportExportServiceImpl implements ReportExportService {

	private final TransactionRepository transactionRepository;
	private final CategoryRepository categoryRepository;
	
	@Override
	public byte[] exportAsCsv(String userId, LocalDate startDate, LocalDate endDate) {
		
        log.info("Generating CSV export for user {} from {} to {}", userId, startDate, endDate);
        
        // Fetch All Tramsactions
        List<TransactionEntity> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
        
        // Sort transaction by date descending
        transactions.sort((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);
        
        // CSV Header
        writer.println("Date,Category Name,Type,Description,Amount");
        
        // CSV rows
        for(TransactionEntity tx : transactions) {
        	
        	CategoryEntity category = categoryRepository.findByCategoryId(tx.getCategoryId())
        			.orElse(null);
        	
        	String categoryName = (category != null) ? category.getName():"UNKNOWN";
        	String categoryType = (category != null) ? category.getType().toString():"UNKNOWN TYPE";
        	
        	writer.printf("%s,%s,%s,%s,%.2f%n",
        			tx.getTransactionDate(),
        			categoryName,
        			categoryType,
                    tx.getDescription() != null ? tx.getDescription() : "",
        			tx.getAmount()
        	);
        }
        
        writer.flush();
        
        log.info("CSV export generated with {} transactions", transactions.size());
        
        return outputStream.toByteArray();
	}

	@Override
	public byte[] exportAsExcel(String userId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating Excel export for user {} from {} to {}", userId, startDate, endDate);
        
        // Fetch All Tramsactions
        List<TransactionEntity> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
        
        // Sort transaction by date descending
        transactions.sort((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));

        // 2. Create a new Excel workbook (.xlsx)
        try(Workbook workbook = new XSSFWorkbook();
        		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();){
        	
        	// 3. Create a sheet
        	Sheet sheet = workbook.createSheet("Transactions");
        	
        	// ✅ Create header style (bold + background)
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // ✅ Create currency style for amount
            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("₹#,##0.00")); // or "$#,##0.00"
            
        	
        	// 4. Create Header Row
        	Row headerRow = sheet.createRow(0);
        	String[] headers = {"Date","Category Name","Type","Description","Amount"};
        	
        	for(int i = 0; i < headers.length; i++) {
        		Cell cell = headerRow.createCell(i);
        		cell.setCellValue(headers[i]);
        		cell.setCellStyle(headerStyle);
        	}
        	
        	// 5. Fill Data Row
        	int rowIndex = 1;
        	for(TransactionEntity tx: transactions) {
        		Row row = sheet.createRow(rowIndex++);
        		
        		CategoryEntity category = categoryRepository.findByCategoryId(tx.getCategoryId())
        				.orElse(CategoryEntity.builder()
        						.name("UNKNOWN")
        						.type(null)
        						.build());
        		
        		row.createCell(0).setCellValue(tx.getTransactionDate().toString());
        		row.createCell(1).setCellValue(category.getName());
        		row.createCell(2).setCellValue(category.getType() != null ? category.getType().toString() : "UNKNOWN");
        		row.createCell(3).setCellValue(tx.getDescription());
        		
        		Cell amountCell = row.createCell(4);
        		amountCell.setCellValue(tx.getAmount());
        		amountCell.setCellStyle(currencyStyle);
        	}
        	
        	// 6. Auto-size columns for better readability
    		for(int i = 0; i < headers.length; i++) {
    			sheet.autoSizeColumn(i);
    		}
    		
            // 7. Write to outputStream
    		workbook.write(outputStream);
    		
    		return outputStream.toByteArray();
        	
        } catch(Exception e) {
        	log.error("Error generating Excel report", e);
        	throw new RuntimeException("Failed to export Excel",e);
        }
	}

	@Override
	public byte[] exportAsPdf(String userId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating PDF export for user {} from {} to {}", userId, startDate, endDate);
        
        try {
        	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        	
            // Step 1: Create document
        	Document document = new Document(PageSize.A4);
        	
            // Step 2: Bind writer
        	PdfWriter.getInstance(document, outputStream);
        	
            // Step 3: Open document
        	document.open();
        	
            // Step 4: Add Title
        	com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, com.lowagie.text.Font.BOLD);
        	Paragraph title = new Paragraph("Transaction Report", titleFont);
        	title.setAlignment(Element.ALIGN_CENTER);
        	document.add(title);
        	
        	// Add date range
        	com.lowagie.text.Font smallFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, com.lowagie.text.Font.ITALIC);
        	document.add(new Paragraph(
        			"From: " + startDate + " To: " + endDate, smallFont
        			));
        	
        	document.add(Chunk.NEWLINE);
        	
        	// Step 5: Fetch all Transactions
        	// Fetch All Tramsactions
            List<TransactionEntity> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
            log.info("Fetched {} transactions for PDF export", transactions.size());

            // Sort transaction by date descending
            transactions.sort((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));
    		
            // Table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            
            // Headers
            addTableHeader(table, "ID");
            addTableHeader(table, "Date");
            addTableHeader(table, "Category");
            addTableHeader(table, "Type");
            addTableHeader(table, "Amount");
            
            // Totals
            double totalIncome = 0;
            double totalExpense = 0;
            
            // Rows
            for(TransactionEntity tx: transactions) { 
            	
            	CategoryEntity category = categoryRepository.findByCategoryId(tx.getCategoryId())
        				.orElse(CategoryEntity.builder()
        						.name("UNKNOWN")
        						.type(null)
        						.build());
            	
                log.debug("Adding transaction: {} | {} | {} | {}", tx.getTransactionDate(), category.getName(), tx.getAmount(), category.getType());
            	
                table.addCell(tx.getTransactionId());
                table.addCell(tx.getTransactionDate().toString());
                table.addCell(category.getName());
                table.addCell(category.getType().toString());
                table.addCell(String.valueOf(tx.getAmount()));
                
                // Accumulate Totals
                if(category.getType() == CategoryType.INCOME) {
                	totalIncome += tx.getAmount();
                } else if(category.getType() == CategoryType.EXPENSE) {
                	totalExpense += tx.getAmount();
                }               
            }
            
            document.add(table);
            
            // Add Total Summary
            document.add(Chunk.NEWLINE);
            com.lowagie.text.Font boldFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, com.lowagie.text.Font.BOLD);
            
            document.add(new Paragraph("Summary", boldFont));
            document.add(new Paragraph("Total Income: " + totalIncome, smallFont));
            document.add(new Paragraph("Total Expense: " + totalExpense, smallFont));
            document.add(new Paragraph("Net Balance: " + (totalIncome - totalExpense), smallFont));
            
            log.info("PDF report generated successfully. Income={}, Expense={}, Balance={}",
                    totalIncome, totalExpense, (totalIncome - totalExpense));
            
            document.close();
            
            return outputStream.toByteArray();
        } catch(Exception e) {
        	log.error("Error generating PDF");
        	throw new RuntimeException("Error generating PDF");
        }
	}
	
    // Utility method to add table header with bold style
	private void addTableHeader(PdfPTable table, String headerTitle) {
		com.lowagie.text.Font headFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 16, com.lowagie.text.Font.BOLD);
		PdfPCell header = new PdfPCell(new Phrase(headerTitle, headFont));
		header.setHorizontalAlignment(Element.ALIGN_CENTER);
		header.setBackgroundColor(Color.LIGHT_GRAY);
		table.addCell(header);
	}
	
}