package com.demo.finance_tracker_backend.controller;

import com.demo.finance_tracker_backend.dto.ApiResponse;
import com.demo.finance_tracker_backend.dto.UserRequest;
import com.demo.finance_tracker_backend.dto.UserResponse;
import com.demo.finance_tracker_backend.enums.Role;
import com.demo.finance_tracker_backend.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // Get Profile of Logged in USER/ADMIN
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(){
    	// Extracting username from SearchContext which is when user is logged in
    	String username = SecurityContextHolder.getContext().getAuthentication().getName();
    	log.info("Fetching profile for username={}", username);
    	UserResponse userResponse = userService.getUserByUsername(username);
    	
    	return ResponseEntity.ok(new ApiResponse<>(true, "Profile Fetched Successfully", userResponse));
    }
    
    
    // Update Profile of Logged In USER?ADMIN
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@RequestBody UserRequest request){
    	UserResponse updateUser = userService.updateMyProfle(request);
    	
    	return ResponseEntity.ok(new ApiResponse<>(true, "Profile Updated Successfully", updateUser));
    }
    
    // GET ALL USERS with optional pagination
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<UserResponse> users = userService.getAllUsers(); // For now, full list. Can add Pageable in future
        return new ApiResponse<>(true, "Users fetched successfully", users);
    }

    // GET USERS BY ROLE
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ApiResponse<List<UserResponse>> getUserByRole(
            @RequestParam(value = "role", required = false, defaultValue = "USER") Role role) {
        List<UserResponse> users = userService.getByRole(role);
        return new ApiResponse<>(true, "Users fetched successfully", users);
    }

    // GET USER BY ID
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String userId) {
        UserResponse userResponse = userService.getUserById(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User fetched successfully", userResponse));
    }

    // UPDATE USER
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable String userId,
                                                                @RequestBody UserRequest userRequest) {
        UserResponse updatedUser = userService.updateUser(userId, userRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "User updated successfully", updatedUser));
    }

    // DELETE USER
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null));
    }
}
