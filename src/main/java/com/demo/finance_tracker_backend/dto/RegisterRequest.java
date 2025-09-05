package com.demo.finance_tracker_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
	
	@NotBlank(message="First Name is required")
	private String firstName;
	
	@NotBlank(message="Last Name is required")
	private String lastName;
	
	@NotBlank(message="Email is required")
	@Email(message="Enter a Valid Email")
	private String email;
	
	private String username;
	
	@NotBlank(message="Password is required")
	@Size(min = 6, max = 20,message = "Password must be between 6 and 20 characters")
	private String password;
	
	private String phone;
}