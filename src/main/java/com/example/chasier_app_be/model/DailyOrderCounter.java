package com.example.chasier_app_be.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "daily_order_counter")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyOrderCounter {
    @Id
    private String id;
    private int seq;
}
