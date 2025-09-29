package com.demo.finance_tracker_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

	private boolean status; // true = success, false = error
	private String message; // informative message
	private T data; // optional payload

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Instant timestamp; // always stored UTC, displayed as IST
	// Static helper for successful responses

	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<>(true, message, data, Instant.now());
	}

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, "Operation successful", data, Instant.now());
	}

	public static <T> ApiResponse<T> success(String message) {
		return new ApiResponse<>(true, message, null, Instant.now());
	}

	// Error with only message
	public static <T> ApiResponse<T> error(String message) {
		return new ApiResponse<>(false, message, null, Instant.now());
	}

	// Error with message + data (like validation errors)
	public static <T> ApiResponse<T> error(String message, T data) {
		return new ApiResponse<>(false, message, data, Instant.now());
	}

}
