package com.demo.finance_tracker_backend.assembler;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.stereotype.Component;

import com.demo.finance_tracker_backend.controller.CategoryController;
import com.demo.finance_tracker_backend.dto.CategoryResponse;
import com.demo.finance_tracker_backend.entity.CategoryEntity;

@Component
public class CategoryResponseAssembler implements RepresentationModelAssembler<CategoryEntity, CategoryResponse>{

	@Override
	public CategoryResponse toModel(CategoryEntity entity) {
		
		CategoryResponse dto = CategoryResponse.builder()
				.categoryId(entity.getCategoryId())
				.userId(entity.getUserId())
				.name(entity.getName())
				.type(entity.getType())
				.isDefault(entity.isDefault())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.build();
		
		// Add a self link to GET /api/categories/{categoryId}
		dto.add(linkTo(methodOn(CategoryController.class).getCategoryById(entity.getCategoryId()))
				.withSelfRel().withType("GET"));
		
		// For non-default categories → add update + delete links
        if (!entity.isDefault()) {
            dto.add(linkTo(methodOn(CategoryController.class).updateCategory(entity.getCategoryId(), null))
                    .withRel("update").withType("PUT"));
            dto.add(linkTo(methodOn(CategoryController.class).deleteCategory(entity.getCategoryId()))
                    .withRel("delete").withType("DELETE"));
        }
        
		return dto;
	}

}
