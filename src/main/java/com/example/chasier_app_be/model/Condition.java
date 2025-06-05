package com.example.chasier_app_be.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Condition {
    // for promo product
    private List<String> productIds;

    // for promo category
    private List<String> categoryIds;

    // for promo buy x get y
    private Integer buyQty;
    private Integer getQty;

    private Double minTotalAmount;

}
