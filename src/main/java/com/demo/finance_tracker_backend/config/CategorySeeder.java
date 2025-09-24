package com.demo.finance_tracker_backend.config;

import java.time.Instant;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.demo.finance_tracker_backend.entity.CategoryEntity;
import com.demo.finance_tracker_backend.enums.CategoryType;
import com.demo.finance_tracker_backend.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategorySeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        seedDefaultCategories();
    }

    private void seedDefaultCategories() {
        // Define system categories
        List<CategoryEntity> defaultCategories = List.of(
                CategoryEntity.builder()
                        .categoryId("SYS-INCOME-1")
                        .name("Salary")
                        .type(CategoryType.INCOME)
                        .isDefault(true)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build(),
                CategoryEntity.builder()
                        .categoryId("SYS-EXPENSE-1")
                        .name("Travel")
                        .type(CategoryType.EXPENSE)
                        .isDefault(true)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build(),
                CategoryEntity.builder()
                        .categoryId("SYS-EXPENSE-2")
                        .name("Food")
                        .type(CategoryType.EXPENSE)
                        .isDefault(true)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build(),
                CategoryEntity.builder()
                        .categoryId("SYS-EXPENSE-3")
                        .name("Entertainment")
                        .type(CategoryType.EXPENSE)
                        .isDefault(true)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()
        );

        // Save only if not already present
        defaultCategories.forEach(category -> {
            boolean exists = categoryRepository.existsByNameAndTypeAndIsDefault(
                    category.getName(), category.getType(), true);
            if (!exists) {
                categoryRepository.save(category);
            }
        });
    }
}
