package com.example.chasier_app_be.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.example.chasier_app_be.dto.CategoryDTO;
import com.example.chasier_app_be.model.Category;
import com.example.chasier_app_be.repository.CategoryRepository;
import com.example.chasier_app_be.util.NotFoundException;
import com.example.chasier_app_be.util.ValidationException;

@Service
public class CategoryService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    CategoryRepository categoryRepository;

    public CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    };

    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = this.categoryRepository.findIsActive();
        if (categories == null || categories.isEmpty()) {
            throw new NotFoundException("Category not found");
        }
        return categories.stream().map(this::convertToDTO).toList();
    }

    public List<CategoryDTO> getCategoryPaginate(int offset, int limit) {
        Query query = new Query()
                .addCriteria(Criteria.where("isActive").is(true))
                .skip(offset)
                .limit(limit)
                .with(Sort.by(Sort.Direction.ASC, "name"));

        List<CategoryDTO> categories = mongoTemplate.find(query, Category.class).stream().map(this::convertToDTO)
                .toList();

        if (categories == null || categories.isEmpty()) {
            throw new NotFoundException("Category not found");
        }
        return categories;
    }

    public long countCategory() {
        Query query = new Query(Criteria.where("isActive").is(true));
        return mongoTemplate.count(query, Category.class);
    }

    public CategoryDTO getById(String id) {
        Category category = this.categoryRepository.findId(id).orElse(null);
        if (category == null) {
            throw new NotFoundException("Category not found");
        }
        return convertToDTO(category);
    }

    public void addCategory(Category category) {
        Map<String, String> errors = new HashMap<>();
        List<Category> existingCategorieName = this.categoryRepository.findName(category.getName());
        System.out.println(existingCategorieName);
        if (!existingCategorieName.isEmpty()) {
            errors.put("name", "Category name already exists.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation error", errors);
        }
        category.setCreatedAt(new Date());
        this.categoryRepository.save(category);
    }

    public void updateCategory(String id, Category category) {
        Map<String, String> errors = new HashMap<>();
        List<Category> existingCategorieName = this.categoryRepository.findExistingName(category.getName(), id);
        if (!existingCategorieName.isEmpty()) {
            errors.put("name", "Category name already exists.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation error", errors);
        }
        Category existingCategory = this.categoryRepository.findId(id).orElse(null);
        if (existingCategory == null) {
            throw new NotFoundException("Category not found");
        }
        existingCategory.setName(category.getName());
        existingCategory.setUpdatedAt(new Date());
        this.categoryRepository.save(existingCategory);
    }

    public void deleteCategory(String id) {
        Category existingCategory = this.categoryRepository.findId(id).orElse(null);
        if (existingCategory == null) {
            throw new NotFoundException("Category not found");
        }
        existingCategory.setIsActive(false);
        existingCategory.setUpdatedAt(new Date());
        this.categoryRepository.save(existingCategory);
    }
}
