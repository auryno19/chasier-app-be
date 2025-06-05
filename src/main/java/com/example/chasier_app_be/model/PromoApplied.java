package com.example.chasier_app_be.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromoApplied {

    private String promoId;
    private String promoName;
    private double discountAmount;

}
