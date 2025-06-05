package com.example.chasier_app_be.dto;

import com.example.chasier_app_be.model.Promotion.PromoTarget;
import com.example.chasier_app_be.model.Promotion.PromoType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDTO {
    private String name;
    private PromoType type;
    private PromoTarget target;
    private double value;
    private ConditionDTO condition;
}
