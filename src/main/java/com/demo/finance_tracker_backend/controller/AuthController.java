package com.demo.finance_tracker_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                .body(ApiResponse.success("User created successfully", userResponse));
    }

    // Verify Email
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token){
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully"));
    }

    // Recover Username through Email
    @PostMapping("/forgot-username")
    public ResponseEntity<ApiResponse<Void>> forgotUsername(@RequestParam String email){
        authService.sendUsernameToEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Username sent to registered email"));
    }

    // Forgot Password
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email){
        authService.forgotPassword(email);
        return ResponseEntity.ok(ApiResponse.success("Password reset token sent to registered email"));
    }

    // Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        authService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.loginUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful", token));
    }
}
