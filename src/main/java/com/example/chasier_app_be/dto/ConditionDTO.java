package com.example.chasier_app_be.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConditionDTO {
    // for promo product
    private List<ProductDTO> products;

    // for promo category
    private List<CategoryDTO> categories;

    // for promo buy x get y
    private Integer buyQty;
    private Integer getQty;

    // for promo min order
    private Double minTotalAmount;

}
