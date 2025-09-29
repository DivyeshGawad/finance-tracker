package com.demo.finance_tracker_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.demo.finance_tracker_backend.enums.Role;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserEntity {

    @Id
    private String id;
    
    private String userId;   // Custom numeric ID (derived from ObjectId)

    private String firstName;

    private String lastName;

    @Indexed(unique = true)
    private String email;   // unique login identifier

    @Indexed(unique = true)
    private String username; // unique login username

    private String password; // encrypted

    private String phone; // optional

    private Role role;
    
 // Email verification fields
    @Builder.Default
    private boolean isVerified = false;
    private String verificationToken;
    private LocalDateTime tokenExpiry;

    // Password Reset feilds
    private String passwordResetToken;
    private LocalDateTime passwordResetTokenExpiry;

    @Builder.Default
    private Integer tokenVersion = 0; // default 0
    
    private Instant createdAt;

    private Instant updatedAt;
}
