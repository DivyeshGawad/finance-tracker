package com.demo.finance_tracker_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.demo.finance_tracker_backend.entity.CategoryEntity;
import com.demo.finance_tracker_backend.enums.CategoryType;

@Repository
public interface CategoryRepository extends MongoRepository<CategoryEntity, String> {
	
	 // Fetch by custom categoryId (business ID)
    Optional<CategoryEntity> findByCategoryId(String categoryId);

    // Fetch all categories by userId (custom categories of a user)
    List<CategoryEntity> findByUserId(String userId);
    
    // Delete By CategoryId
    void deleteByCategoryId(String categoryId);

    // Fetch system (default) categories only
    List<CategoryEntity> findByIsDefaultTrue();

    // Fetch categories by type (INCOME / EXPENSE)
    List<CategoryEntity> findByType(CategoryType type);

    // Check if category with same name exists for a user (avoid duplicates)
    boolean existsByNameAndUserId(String name, String userId);

    // For Service level filtering
    List<CategoryEntity> findByUserIdAndType(String userId, CategoryType type);
    
    // ✅ Fetch ALL categories (default + user’s) — useful for reports
    @Query("{ '$or': [ { 'isDefault': true }, { 'userId': ?0 } ] }")
    List<CategoryEntity> findAllForUser(String userId);
    
    // Search default + user categories with optional name/type filters
    @Query("{ '$and': [ " +
            "  { '$or': [ { 'isDefault': true }, { 'userId': ?0 } ] }, " +
            "  { 'name': { $regex: ?1, $options: 'i' } }, " +
            "  { 'type': { $regex: ?2, $options: 'i' } } " +
            "] }")
    Page<CategoryEntity> searchCategories(String userId, String name, String type, Pageable pageable);

	boolean existsByNameAndTypeAndIsDefault(String name, CategoryType type, boolean b);
}
