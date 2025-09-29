package com.demo.finance_tracker_backend.service;

import com.demo.finance_tracker_backend.dto.LoginRequest;
import com.demo.finance_tracker_backend.dto.UserRequest;
import com.demo.finance_tracker_backend.dto.UserResponse;

public interface AuthService {

	UserResponse registerUser(UserRequest request);

	String loginUser(LoginRequest loginRequest);

	void verifyEmail(String token);

	void sendUsernameToEmail(String email);

	void forgotPassword(String email);

	void resetPassword(String token, String newPassword);

	
}
