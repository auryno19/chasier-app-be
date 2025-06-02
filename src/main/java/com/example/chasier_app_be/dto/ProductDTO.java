package com.example.chasier_app_be.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDTO {
    private String id;
    @NotNull(message = "Name is required")
    @NotBlank(message = "Name is required")
    private String name;
    @DecimalMin(value = "0.01", inclusive = true, message = "Price is required")
    private Double price;
    @Min(value = 1, message = "Stock is required")
    private Integer stock;
    @NotNull(message = "Category is required")
    @NotBlank(message = "Category is required")
    private String category;
    private String slug;
}
