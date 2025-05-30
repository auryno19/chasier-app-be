package com.example.chasier_app_be.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.chasier_app_be.dto.CategoryDTO;
import com.example.chasier_app_be.model.Category;
import com.example.chasier_app_be.service.CategoryService;
import com.example.chasier_app_be.util.ApiResponse;
import com.example.chasier_app_be.util.NotFoundException;
import com.example.chasier_app_be.util.ResponseUtil;
import com.example.chasier_app_be.util.ValidationException;

import jakarta.validation.Valid;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/category")
public class ApiCategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<Object>> getAllCategories() {
        try {
            List<CategoryDTO> categories = this.categoryService.getAllCategories();
            return ResponseUtil.generateSuccessResponse("Get All Data Success", categories, HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseUtil.generateErrorResponse("Category not found", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Get data failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/paginate")
    public ResponseEntity<ApiResponse<Object>> getCategoryPaginate(
            @RequestParam(defaultValue = "1", name = "page") int page,
            @RequestParam(defaultValue = "5", name = "per_page") int per_page,
            @RequestParam(defaultValue = "", name = "search") String search) {
        try {
            int total = (int) this.categoryService.countCategory();
            int total_pages = total % per_page == 0 ? total / per_page : (total / per_page) + 1;
            int offset = (page - 1) * per_page;
            List<CategoryDTO> categories = this.categoryService.getCategoryPaginate(offset, per_page, search);
            Map<String, Object> response = new HashMap<>();
            response.put("page", page);
            response.put("per_page", per_page);
            response.put("total", total);
            response.put("total_pages", total_pages);
            response.put("datas", categories);
            return ResponseUtil.generateSuccessResponse("Get Data Success", response, HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseUtil.generateErrorResponse("Category not found", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Get data failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getCategoryById(String id) {
        try {
            CategoryDTO category = this.categoryService.getById(id);
            return ResponseUtil.generateSuccessResponse("Get Data Success", category, HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseUtil.generateErrorResponse("Category not found", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Get data failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("add")
    public ResponseEntity<ApiResponse<Object>> addCategory(@Valid @RequestBody Category category) {
        try {
            this.categoryService.addCategory(category);
            return ResponseUtil.generateSuccessResponse("Add Data Success", null, HttpStatus.CREATED);
        } catch (ValidationException e) {
            return ResponseUtil.generateErrorResponse("Validation error", e.getErrors(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Add data failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateCategory(@PathVariable String id,
            @Valid @RequestBody Category category) {
        try {
            this.categoryService.updateCategory(id, category);
            return ResponseUtil.generateSuccessResponse("Update Data Success", null, HttpStatus.OK);
        } catch (ValidationException e) {
            return ResponseUtil.generateErrorResponse("Validation error", e.getErrors(), HttpStatus.CONFLICT);
        } catch (NotFoundException e) {
            return ResponseUtil.generateErrorResponse("Category not found", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Update data failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteCategory(@PathVariable String id) {
        try {
            this.categoryService.deleteCategory(id);
            return ResponseUtil.generateSuccessResponse("Delete Data Success", null, HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseUtil.generateErrorResponse("Category not found", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Delete data failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
