package com.demo.finance_tracker_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.finance_tracker_backend.dto.ApiResponse;
import com.demo.finance_tracker_backend.dto.LoginRequest;
import com.demo.finance_tracker_backend.dto.ResetPasswordRequest;
import com.demo.finance_tracker_backend.dto.UserRequest;
import com.demo.finance_tracker_backend.dto.UserResponse;
import com.demo.finance_tracker_backend.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	
	// Register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody UserRequest request) {
        UserResponse userResponse = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User created successfully", userResponse));
    }

    // Verify Email
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token){
    	authService.verifyEmail(token);
    	return ResponseEntity.ok(new ApiResponse<>(true, "Email Verified Successfully",null));
    }
    
    // Recover Username through Email
    @PostMapping("/forgot-username")
    public ResponseEntity<ApiResponse<Void>> forgotUsername(@RequestParam String email){
    	authService.sendUsernameToEmail(email);
    	
    	return ResponseEntity.ok(new ApiResponse<>(true, "Username has send to registered Email", null));
    }
    
    // Forgot Password
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email){
    	authService.forgotPassword(email);
    	return ResponseEntity.ok(new ApiResponse<>(true,"Password Reset token sent to your registered email",null));
    }
    
    // Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
    	authService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
    	
    	return ResponseEntity.ok(new ApiResponse<>(true,"Password Reset Successfully",null));
    }
    
    // Login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.loginUser(loginRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", token));
    }
}
