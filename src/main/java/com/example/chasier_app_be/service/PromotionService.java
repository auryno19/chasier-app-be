package com.example.chasier_app_be.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.example.chasier_app_be.dto.CategoryDTO;
import com.example.chasier_app_be.dto.ConditionDTO;
import com.example.chasier_app_be.dto.ProductDTO;
import com.example.chasier_app_be.dto.PromotionDTO;
import com.example.chasier_app_be.model.Category;
import com.example.chasier_app_be.model.Condition;
import com.example.chasier_app_be.model.Product;
import com.example.chasier_app_be.model.Promotion;
import com.example.chasier_app_be.repository.CategoryRepository;
import com.example.chasier_app_be.repository.ProductRepository;
import com.example.chasier_app_be.repository.PromotionRepository;
import com.example.chasier_app_be.util.NotFoundException;
import com.example.chasier_app_be.util.ValidationException;

@Service
public class PromotionService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    PromotionRepository promotionRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryService categoryService;

    public ConditionDTO convertToConditionDTO(Condition condition) {
        ConditionDTO dto = new ConditionDTO();
        if (condition.getProductIds() != null) {
            List<ProductDTO> products = condition.getProductIds().stream().map(a -> {
                Product product = this.productRepository.findId(a)
                        .orElse(null);
                ProductDTO productDTO = productService.convertToDTo(product);
                return productDTO;
            }).toList();

            dto.setProducts(products);
        }
        if (condition.getCategoryIds() != null) {
            List<CategoryDTO> categories = condition.getCategoryIds().stream().map(a -> {
                Category category = this.categoryRepository.findId(a).orElse(null);
                CategoryDTO categoryDTO = categoryService.convertToDTO(category);
                return categoryDTO;
            }).toList();

            dto.setCategories(categories);
        }

        dto.setBuyQty(condition.getBuyQty());
        dto.setGetQty(condition.getGetQty());
        dto.setMinTotalAmount(condition.getMinTotalAmount());
        return dto;
    }

    public PromotionDTO convertToDTo(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        ConditionDTO condition = this.convertToConditionDTO(promotion.getCondition());
        dto.setName(promotion.getName());
        dto.setType(promotion.getType());
        dto.setTarget(promotion.getTarget());
        dto.setValue(promotion.getValue());
        dto.setCondition(condition);
        return dto;
    }

    public void checkedPromo() {
        Instant now = Instant.now();

        Query query = new Query();
        query.addCriteria(Criteria.where("endDate").lte(now).and("isActive").is(true));

        Update update = new Update().set("isActive", false);

        mongoTemplate.updateMulti(query, update, Promotion.class);
    }

    public List<Promotion> getActivePromo() {
        this.checkedPromo();
        List<Promotion> promos = this.promotionRepository.findIsActive();
        if (promos == null || promos.isEmpty()) {
            throw new NotFoundException("Promotion not found");
        }
        return promos;
    }

    public List<PromotionDTO> getAllPromo() {
        this.checkedPromo();
        List<Promotion> promos = this.promotionRepository.findIsActive();
        if (promos == null || promos.isEmpty()) {
            throw new NotFoundException("Promotion not found");
        }
        return promos.stream().map(this::convertToDTo).toList();
    }

    public void addPromo(Promotion promo) {
        Map<String, String> errors = new HashMap<>();
        List<Promotion> existingPRomo = this.promotionRepository.findName(promo.getName());

        if (existingPRomo != null && !existingPRomo.isEmpty()) {
            errors.put("name", "Promotion name already exists");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation error", errors);
        }
        this.promotionRepository.save(promo);
    }

}
