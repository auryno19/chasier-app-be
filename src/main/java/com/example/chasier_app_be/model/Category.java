package com.example.chasier_app_be.model;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    private String id;

    @NotNull(message = "Name is required")
    @NotBlank(message = "Name is required")
    private String name;
    private Boolean isActive = true;

    @CreatedDate
    private Date createdAt;
    private Date updatedAt;
}
