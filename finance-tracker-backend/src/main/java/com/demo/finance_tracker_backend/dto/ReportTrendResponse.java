package com.demo.finance_tracker_backend.dto;

import jakarta.validation.constraints.NegativeOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NegativeOrZero
public class ReportTrendResponse {

	private String month;     // format "YYYY-MM"
    private double income;
    private double expense;
}
