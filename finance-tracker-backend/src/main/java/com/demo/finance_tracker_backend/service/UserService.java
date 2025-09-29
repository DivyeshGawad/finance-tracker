package com.demo.finance_tracker_backend.service;

import java.util.List;

import com.demo.finance_tracker_backend.dto.UserRequest;
import com.demo.finance_tracker_backend.dto.UserResponse;
import com.demo.finance_tracker_backend.enums.Role;

public interface UserService {
	

	List<UserResponse> getAllUsers();

	List<UserResponse> getByRole(Role role);

	UserResponse getUserById(String userId);

	public UserResponse updateUser(String userId, UserRequest request);

	void deleteUser(String userId);

	UserResponse getUserByUsername(String username);
	
	UserResponse updateMyProfle(UserRequest request);
}