package com.demo.finance_tracker_backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.demo.finance_tracker_backend.entity.TransactionEntity;

@Repository
public interface TransactionRepository extends MongoRepository<TransactionEntity, String> {

    // Fetch single transaction by business ID
    Optional<TransactionEntity> findByTransactionId(String transactionId);

    // Fetch all transactions of a user with pagination
    Page<TransactionEntity> findByUserId(String userId, Pageable pageable);

    // Delete transaction by business ID
    void deleteByTransactionId(String transactionId);

}