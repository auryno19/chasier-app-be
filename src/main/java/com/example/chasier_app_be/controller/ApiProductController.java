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

import com.example.chasier_app_be.dto.ProductDTO;
import com.example.chasier_app_be.model.Product;
import com.example.chasier_app_be.service.ProductService;
import com.example.chasier_app_be.util.ApiResponse;
import com.example.chasier_app_be.util.NotFoundException;
import com.example.chasier_app_be.util.ResponseUtil;
import com.example.chasier_app_be.util.ValidationException;

import jakarta.validation.Valid;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/product")
public class ApiProductController {

    @Autowired
    ProductService productService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<Object>> getAllProducts() {
        try {
            List<ProductDTO> users = this.productService.getAllProdutcs();
            return ResponseUtil.generateSuccessResponse("Get All Data Success",
                    users,
                    HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseUtil.generateErrorResponse("Product not found", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Get data failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/paginate")
    public ResponseEntity<ApiResponse<Object>> getProductPaginate(
            @RequestParam(defaultValue = "1", name = "page") int page,
            @RequestParam(defaultValue = "5", name = "per_page") int per_page) {
        try {
            int total = (int) this.productService.countProduct();
            int total_pages = total % per_page == 0 ? total / per_page : (total / per_page) + 1;
            int offset = (page - 1) * per_page;
            List<ProductDTO> products = this.productService.getProductPaginate(offset, per_page);
            Map<String, Object> response = new HashMap<>();
            response.put("page", page);
            response.put("per_page", per_page);
            response.put("total", total);
            response.put("total_pages", total_pages);
            response.put("datas", products);
            return ResponseUtil.generateSuccessResponse("Get Data Success", response, HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseUtil.generateErrorResponse("Product not found", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Get data failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getProductById(@PathVariable String id) {
        try {
            ProductDTO product = this.productService.getById(id);
            return ResponseUtil.generateSuccessResponse("Get Data Success", product, HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseUtil.generateErrorResponse("Product not found", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Get data failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("add")
    public ResponseEntity<ApiResponse<Object>> addProduct(@Valid @RequestBody Product product) {
        try {
            this.productService.addProduct(product);
            return ResponseUtil.generateSuccessResponse("Add Data Success", null, HttpStatus.CREATED);
        } catch (ValidationException e) {
            return ResponseUtil.generateErrorResponse("Validation error", e.getErrors(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Add data failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateProduct(@PathVariable String id, @RequestBody Product product) {
        try {
            this.productService.updateProduct(id, product);
            return ResponseUtil.generateSuccessResponse("Update Data Success", null, HttpStatus.OK);
        } catch (ValidationException e) {
            return ResponseUtil.generateErrorResponse("Validation error", e.getErrors(), HttpStatus.CONFLICT);
        } catch (NotFoundException e) {
            return ResponseUtil.generateErrorResponse("Product not found", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Update data failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteProduct(@PathVariable String id) {
        try {
            this.productService.deleteProduct(id);
            return ResponseUtil.generateSuccessResponse("Delete Data Success", null, HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseUtil.generateErrorResponse("Product not found", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Delete data failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
