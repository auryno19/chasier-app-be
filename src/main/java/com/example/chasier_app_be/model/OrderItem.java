package com.example.chasier_app_be.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    private String productId;
    private String productName;
    private double price;
    private int quantity;
    private double totalPrice;
    private PromoApplied promoApplied;

}
