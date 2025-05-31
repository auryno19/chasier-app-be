package com.example.chasier_app_be.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.example.chasier_app_be.dto.ProductDTO;
import com.example.chasier_app_be.model.Category;
import com.example.chasier_app_be.model.Product;
import com.example.chasier_app_be.repository.CategoryRepository;
import com.example.chasier_app_be.repository.ProductRepository;
import com.example.chasier_app_be.util.NotFoundException;
import com.example.chasier_app_be.util.ValidationException;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    private String convertToSlug(String str) {
        return str.replaceAll("[^a-zA-Z0-9]", "-").toLowerCase();
    }

    public ProductDTO convertToDTo(Product product) {
        ProductDTO dto = new ProductDTO();
        Category category = categoryRepository.findId(product.getCategoryId()).orElse(null);
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCategory(category.getName());
        dto.setSlug(product.getSlug());
        return dto;
    }

    public List<ProductDTO> getAllProdutcs() {
        List<Product> products = this.productRepository.findIsActive();
        if (products == null || products.isEmpty()) {
            throw new NotFoundException("Product not found");
        }
        return products.stream().map(this::convertToDTo).toList();
    }

    public List<ProductDTO> getProductPaginate(int offset, int limit) {
        Query query = new Query()
                .skip(offset)
                .limit(limit)
                .with(Sort.by(Sort.Direction.ASC, "name"));

        List<ProductDTO> products = mongoTemplate.find(query, Product.class).stream().map(this::convertToDTo).toList();

        if (products == null || products.isEmpty()) {
            throw new NotFoundException("Product not found");
        }

        return products;
    }

    public long countProduct() {
        return this.productRepository.count();
    }

    public ProductDTO getById(String id) {
        Product product = this.productRepository.findId(id).orElse(null);
        if (product == null) {
            throw new NotFoundException("Product not found");
        }
        return convertToDTo(product);
    }

    public ProductDTO getByCategory(String categoryId) {
        List<Product> products = this.productRepository.findCategoryId(categoryId);
        if (products == null || products.isEmpty()) {
            throw new NotFoundException("Product not found");
        }
        return products.stream().map(this::convertToDTo).toList().get(0);
    }

    public void addProduct(Product product) {
        Map<String, String> errors = new HashMap<>();
        List<Product> existingProducts = this.productRepository.findName(product.getName());
        if (!existingProducts.isEmpty()) {
            errors.put("name", "Product name already exists.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation error", errors);
        }
        product.setSlug(convertToSlug(product.getName()));
        product.setCreatedAt(new Date());
        this.productRepository.save(product);
    }

    public void updateProduct(String id, Product product) {
        Product existingProduct = this.productRepository.findId(id).orElse(null);
        Map<String, String> errors = new HashMap<>();
        List<Product> existingProductName = this.productRepository.findExistingName(product.getName(), id);
        if (!existingProductName.isEmpty()) {
            errors.put("name", "Product name already exists.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation error", errors);
        }
        if (existingProduct != null) {

            existingProduct.setName(product.getName());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setCategoryId(product.getCategoryId());
            existingProduct.setSlug(convertToSlug(product.getName()));
            existingProduct.setUpdatedAt(new Date());
            this.productRepository.save(existingProduct);
        } else {
            throw new NotFoundException("Product not found");
        }
    }

    public void deleteProduct(String id) {
        Product existingProduct = this.productRepository.findId(id).orElse(null);
        if (existingProduct != null) {
            existingProduct.setStock(0);
            existingProduct.setUpdatedAt(new Date());
            existingProduct.setIsActive(false);
            this.productRepository.save(existingProduct);
        } else {
            throw new NotFoundException("Product not found");
        }
    }
}
