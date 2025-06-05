package com.example.chasier_app_be.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chasier_app_be.model.Order;
import com.example.chasier_app_be.service.OrderService;
import com.example.chasier_app_be.util.ApiResponse;
import com.example.chasier_app_be.util.ResponseUtil;
import com.example.chasier_app_be.util.ValidationException;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/order")
public class ApiOrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Object>> addOrder(@RequestBody Order order) {
        try {
            String orderId = this.orderService.manageOrder(order);
            Map<String, String> response = Map.of("orderId", orderId);
            return ResponseUtil.generateSuccessResponse("Order added successfully", response, HttpStatus.CREATED);
        } catch (ValidationException e) {
            return ResponseUtil.generateErrorResponse("Failed to add order", e.getErrors(),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Failed to add order", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Object>> updateOrder(@RequestBody Order order) {
        try {
            String orderId = this.orderService.manageOrder(order);
            Map<String, String> response = Map.of("orderId", orderId);
            return ResponseUtil.generateSuccessResponse("Order added successfully", response, HttpStatus.CREATED);
        } catch (ValidationException e) {
            return ResponseUtil.generateErrorResponse("Failed to add order", e.getErrors(),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Failed to add order", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
