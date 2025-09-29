package com.demo.finance_tracker_backend.serviceImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.finance_tracker_backend.dto.LoginRequest;
import com.demo.finance_tracker_backend.dto.UserRequest;
import com.demo.finance_tracker_backend.dto.UserResponse;
import com.demo.finance_tracker_backend.entity.UserEntity;
import com.demo.finance_tracker_backend.enums.Role;
import com.demo.finance_tracker_backend.event.EmailChangeEvent;
import com.demo.finance_tracker_backend.exception.EmailAlreadyExistsException;
import com.demo.finance_tracker_backend.exception.InvalidTokenException;
import com.demo.finance_tracker_backend.exception.UnauthorizedException;
import com.demo.finance_tracker_backend.exception.UserNotFoundException;
import com.demo.finance_tracker_backend.exception.UsernameAlreadyExistsException;
import com.demo.finance_tracker_backend.repository.UserRepository;
import com.demo.finance_tracker_backend.service.AuthService;
import com.demo.finance_tracker_backend.util.IdGeneratorUtil;
import com.demo.finance_tracker_backend.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final ApplicationEventPublisher eventPublisher;

	// New User Registration
	@Override
	@Transactional
	public UserResponse registerUser(UserRequest request) {
		// Check for unique email & username
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("Email already registered: " + request.getEmail());
		}

		if (userRepository.existsByUsername(request.getUsername())) {
			throw new UsernameAlreadyExistsException("Username already taken: " + request.getUsername());
		}

		Role assignedRole = (request.getRole() != null && request.getRole().equals(Role.ADMIN)) ? Role.ADMIN
				: Role.USER;

		UserEntity user = UserEntity.builder().userId(IdGeneratorUtil.generateMediumNumericId())
				.firstName(request.getFirstName()).lastName(request.getLastName()).email(request.getEmail())
				.username(request.getUsername()).password(passwordEncoder.encode(request.getPassword()))
				.phone(request.getPhone()).role(assignedRole)
				.verificationToken(UUID.randomUUID().toString()).tokenExpiry(LocalDateTime.now().plusHours(24))
				.createdAt(Instant.now()).updatedAt(Instant.now()).build();

		UserEntity savedUser = userRepository.save(user);

		// Publish Registration email event
		eventPublisher.publishEvent(new EmailChangeEvent(user, EmailChangeEvent.EmailType.REGISTRATION, null, null));
		log.info("User {} created with role {}", user.getUsername(), assignedRole);
		return mapToResponse(savedUser);
	}

	// Verification using email token
	@Override
	public void verifyEmail(String token) {
		UserEntity user = userRepository.findByVerificationToken(token)
				.orElseThrow(() -> new InvalidTokenException("The provided token is invalid or expired"));

		user.setVerified(true);
		user.setVerificationToken(null);
		user.setTokenExpiry(null);
		user.setUpdatedAt(Instant.now());

		userRepository.save(user);

		log.info("AUDIT: User {} email verified", user.getUsername());
	}

	// Username Recovery Through email
	@Override
	@Transactional
	public void sendUsernameToEmail(String email) {
		UserEntity user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User not found with email:- " + email));

		eventPublisher
				.publishEvent(new EmailChangeEvent(user, EmailChangeEvent.EmailType.USERNAME_RECOVERY, null, null));
		log.info("AUDIT: Username recovery event published for {}", email);
	}

	// Forgot Password
	@Override
	@Transactional
	public void forgotPassword(String email) {
		UserEntity user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User not found with email:- " + email));

		// Generate Token and set expiry
		user.setPasswordResetToken(UUID.randomUUID().toString());
		user.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(15)); // Valid for 15 minutes

		userRepository.save(user);

		eventPublisher.publishEvent(new EmailChangeEvent(user, EmailChangeEvent.EmailType.PASSSWORD_RESET, null, null));
		log.info("AUDIT: Password reset event published for {}", email);
	}

	// Reset Password
	@Override
	public void resetPassword(String token, String newPassword) {
		UserEntity user = userRepository.findByPasswordResetToken(token)
				.orElseThrow(() -> new InvalidTokenException("The provided token is invalid or expired"));

		if (user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
			throw new InvalidTokenException("The provided token is invalid or expired");
		}

		// Set New Password
		user.setPassword(passwordEncoder.encode(newPassword));

		// CLear reset token
		user.setPasswordResetToken(null);
		user.setPasswordResetTokenExpiry(null);
		 user.setTokenVersion(user.getTokenVersion() + 1); // Invalidate old JWTs

		userRepository.save(user);

		log.info("AUDIT: Password reset successfully for {}", user.getUsername());
	}

	/* Login */
	@Override
	public String loginUser(LoginRequest loginRequest) {
		// Find user by username
		UserEntity user = userRepository.findByUsername(loginRequest.getUsername())
				.orElseThrow(() -> new UnauthorizedException("Invalid username or password")); // avoid revealing if
																								// username exists

		// Check if email is verified
		if (!user.isVerified()) {
			throw new UnauthorizedException("Email is not verified. Please check your inbox.");
		}

		// Check password
		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			log.warn("Invalid login attempt for username {}", loginRequest.getUsername());
			throw new UnauthorizedException("Invalid username or password");
		}

		// Generate JWT token
		String token = jwtUtil.generateToken(user);

		log.info("User {} logged in successfully", loginRequest.getUsername());
		return token;
	}

	// For Single User
	private UserResponse mapToResponse(UserEntity userEntity) {
		return new UserResponse(userEntity.getUserId(), userEntity.getFirstName(), userEntity.getLastName(),
				userEntity.getEmail(), userEntity.getUsername(), userEntity.getPhone(), userEntity.getRole()
				, userEntity.getCreatedAt(), userEntity.getUpdatedAt());
	}
}
