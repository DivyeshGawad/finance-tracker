package com.demo.finance_tracker_backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CategoryType {

	INCOME, // Categories for money coming in (e.g., Salary, Bonus, Investments)
	EXPENSE; // Categories for money going out (e.g., Food, Rent, Bills)
	
	@JsonCreator
	public static CategoryType fromString(String value) {
		if(value == null) {
			return null;
		}
		return CategoryType.valueOf(value.trim().toUpperCase());
	}
}