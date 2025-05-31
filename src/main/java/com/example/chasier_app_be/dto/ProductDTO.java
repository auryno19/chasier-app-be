package com.example.chasier_app_be.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private String id;
    private String name;
    private Double price;
    private Integer stock;
    private String category;
    private String slug;
}
