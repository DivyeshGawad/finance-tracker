package com.demo.finance_tracker_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.demo.finance_tracker_backend.entity.UserEntity;
import com.demo.finance_tracker_backend.enums.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<UserEntity, String> {
	Optional<UserEntity> findByEmail(String email);

	Optional<UserEntity> findByUsername(String username);

	Optional<UserEntity> findByUserId(String userId);

	List<UserEntity> findByRole(Role role);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	Optional<UserEntity> findByVerificationToken(String token);

	Optional<UserEntity> findByPasswordResetToken(String passwordResetToken);

	long countByRole(Role admin);

	// 🆕 New method for cleanup
	void deleteByIsVerifiedFalseAndCreatedAtBefore(LocalDateTime cutoffDate);
}
