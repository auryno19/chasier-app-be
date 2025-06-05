package com.example.chasier_app_be.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chasier_app_be.dto.PromotionDTO;
import com.example.chasier_app_be.model.Promotion;
import com.example.chasier_app_be.service.PromotionService;
import com.example.chasier_app_be.util.ApiResponse;
import com.example.chasier_app_be.util.NotFoundException;
import com.example.chasier_app_be.util.ResponseUtil;
import com.example.chasier_app_be.util.ValidationException;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/promotion")
public class ApiPromotionController {

    @Autowired
    PromotionService promotionService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<Object>> getAllPromotions() {
        try {
            List<PromotionDTO> promotions = this.promotionService.getAllPromo();
            return ResponseUtil.generateSuccessResponse("Get All Data Success", promotions, HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseUtil.generateErrorResponse("Promotion not found", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Get data failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Object>> addPromo(@RequestBody Promotion promo) {
        try {
            this.promotionService.addPromo(promo);
            return ResponseUtil.generateSuccessResponse("Add Promotion Success", null, HttpStatus.CREATED);
        } catch (ValidationException e) {
            return ResponseUtil.generateErrorResponse("Validation error", e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Add data failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
