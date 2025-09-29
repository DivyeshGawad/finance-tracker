package com.demo.finance_tracker_backend.serviceImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.finance_tracker_backend.dto.UserRequest;
import com.demo.finance_tracker_backend.dto.UserResponse;
import com.demo.finance_tracker_backend.entity.UserEntity;
import com.demo.finance_tracker_backend.enums.Role;
import com.demo.finance_tracker_backend.event.EmailChangeEvent;
import com.demo.finance_tracker_backend.exception.EmailAlreadyExistsException;
import com.demo.finance_tracker_backend.exception.UserNotFoundException;
import com.demo.finance_tracker_backend.repository.UserRepository;
import com.demo.finance_tracker_backend.security.CurrentUser;
import com.demo.finance_tracker_backend.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ApplicationEventPublisher eventPublisher;
	private final CurrentUser currentUser;
	
	// Get User By userId
	@Override
	public UserResponse getUserById(String userId) {
		UserEntity user = userRepository.findByUserId(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
		return mapToResponse(user);
	}

	// Get All Users
	@Override
	public List<UserResponse> getAllUsers() {
		List<UserEntity> allUsers = userRepository.findAll();
		return mapToResponse(allUsers);
	}

	// Updating Profile of the LoggedIn User
	@Override
	@Transactional
	public UserResponse updateMyProfle(UserRequest request) {

		// Get the username from SecurityContext
		String currentUsername = currentUser.getUsername();

		UserEntity user = userRepository.findByUsername(currentUsername)
				.orElseThrow(() -> new UserNotFoundException("User Not found with username:- " + currentUsername));

		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setPhone(request.getPhone());

		// Handle email change
		// Updating and sending verification code to new updated email
		handleEmailChange(user, request.getEmail());
		if (request.getPassword() != null && !request.getPassword().isEmpty())

		{
			user.setPassword(passwordEncoder.encode(request.getPassword()));
			 user.setTokenVersion(user.getTokenVersion() + 1); // Invalidate old JWTs
		}

		user.setUpdatedAt(Instant.now());

		log.info("User Profile {} updated", user.getUsername());
		UserEntity updatedUser = userRepository.save(user);
		return

		mapToResponse(updatedUser);
	}

	// Get User By Role ("ADMIN" OR "USER")
	@Override
	public List<UserResponse> getByRole(Role role) {
		List<UserEntity> users = userRepository.findByRole(role);
		return mapToResponse(users);
	}

	// Update User by userId
//	@Override
//	@Transactional
//	public UserResponse updateUser(String userId, UserRequest request) {
//		UserEntity user = userRepository.findByUserId(userId)
//				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
//
//		user.setFirstName(request.getFirstName());
//		user.setLastName(request.getLastName());
//		user.setEmail(request.getEmail());
//		user.setUsername(request.getUsername());
//
//		if (request.getPassword() != null && !request.getPassword().isEmpty()) {
//			user.setPassword(passwordEncoder.encode(request.getPassword()));
//		}
//
//		user.setPhone(request.getPhone());
//
//		if (request.getRole() != null && request.getRole().equals(Role.ADMIN)) {
//			user.setRole(Role.ADMIN);
//		}
//
//		user.setUpdatedAt(LocalDateTime.now());
//		UserEntity updatedUser = userRepository.save(user);
//
//		log.info("User {} updated", user.getUsername());
//		return mapToResponse(updatedUser);
//	}

	// Update User by userId
	@Override
	@Transactional
	public UserResponse updateUser(String userId, UserRequest request) {
		UserEntity user = userRepository.findByUserId(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

		// Update basic details
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setUsername(request.getUsername());

		
		// Update password if provided
		if (request.getPassword() != null && !request.getPassword().isEmpty()) {
			user.setPassword(passwordEncoder.encode(request.getPassword()));
			 user.setTokenVersion(user.getTokenVersion() + 1); // Invalidate old JWTs
		}

		// Update phone
		user.setPhone(request.getPhone());

		// Handle role change safely
		if (request.getRole() != null) {
			Role currentRole = user.getRole();
			Role newRole = request.getRole();

			// Prevent demotion of last admin
			if (currentRole == Role.ADMIN && newRole == Role.USER) {
				long adminCount = userRepository.countByRole(Role.ADMIN);
				log.info("Total Number of Admins - " + adminCount);
				if (adminCount <= 1) {
					throw new IllegalStateException("Cannot demote the last admin");
				}
			}

			// Optional: Prevent self-demotion if needed
			String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
			if (user.getUsername().equals(loggedInUsername) && currentRole == Role.ADMIN && newRole == Role.USER) {
				throw new IllegalStateException("Admin cannot demote themselves");
			}

			user.setRole(newRole);
			log.info("User {} role changed from {} to {}", user.getUsername(), currentRole, newRole);
		}

		// Update audit info
		user.setUpdatedAt(Instant.now());
		
		// Updating and sending verification code to new updated email
		handleEmailChange(user, request.getEmail());

		UserEntity updatedUser = userRepository.save(user);

		log.info("User {} updated successfully", user.getUsername());
		return mapToResponse(updatedUser);
	}

	// Delete User by userId
	@Override
	@Transactional
	public void deleteUser(String userId) {
		UserEntity user = userRepository.findByUserId(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

		userRepository.delete(user);
		log.info("User {} deleted", user.getUsername());
	}

	// Get User Profile By its username
	@Override
	public UserResponse getUserByUsername(String username) {
		UserEntity user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User not found with username:- " + username));

		return mapToResponse(user);
	}

	// For a List of Users like for getAllUsers and get Users by Role
	private List<UserResponse> mapToResponse(List<UserEntity> allUsers) {
		List<UserResponse> responseList = new ArrayList<>();
		for (UserEntity user : allUsers) {
			responseList.add(mapToResponse(user));
		}
		return responseList;
	}

	// For Single User
	private UserResponse mapToResponse(UserEntity userEntity) {
		return new UserResponse(userEntity.getUserId(), userEntity.getFirstName(), userEntity.getLastName(),
				userEntity.getEmail(), userEntity.getUsername(), userEntity.getPhone(), userEntity.getRole(), userEntity.getCreatedAt(), userEntity.getUpdatedAt());
	}
	
	private void handleEmailChange(UserEntity user, String newEmail) {
	    if (newEmail != null && !newEmail.equals(user.getEmail())) {
	        if (userRepository.existsByEmail(newEmail)) {
	            throw new EmailAlreadyExistsException("Email already exists.");
	        }

	        String oldEmail = user.getEmail();
	        String token = UUID.randomUUID().toString();

	        user.setEmail(newEmail);
	        user.setVerified(false);
	        user.setVerificationToken(token);
	        user.setTokenExpiry(LocalDateTime.now().plusHours(24));
	        user.setTokenVersion(user.getTokenVersion() + 1); // Invalidate old JWTs

	        eventPublisher.publishEvent(new EmailChangeEvent(user, EmailChangeEvent.EmailType.EMAIL_UPDATE, oldEmail, newEmail));
	        log.info("AUDIT: Email change event published for {} -> {}", oldEmail, newEmail);
	    }
	}
}
