package com.example.chasier_app_be.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    private String id;
    private String userId;
    private String orderNumber;
    private List<OrderItem> items;
    private double totalAmount;
    private String status;
    private PromoApplied promoApplied;
    @CreatedDate
    private Date createdAt;
}
