package com.example.chasier_app_be.model;

import java.time.Instant;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "promotions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Promotion {

    @Id
    private String id;
    private String name;
    private PromoType type;
    private PromoTarget target;
    private double value;
    private Condition condition;
    private Instant startDate;
    private Instant endDate;
    private boolean isActive = true;
    @CreatedDate
    private Date createdAt;
    private Date updatedAt;

    public enum PromoType {
        PERCENTAGE,
        FIXED,
        BUY_X_GET_Y;

        @JsonCreator
        public static PromoType from(String value) {
            try {
                return PromoType.valueOf(value.toUpperCase());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid promo type: " + value);
            }
        }

        @JsonValue
        public String toValue() {
            return name().toLowerCase();
        }

    }

    public enum PromoTarget {
        PRODUCT,
        CATEGORY,
        ORDER;

        @JsonCreator
        public static PromoType from(String value) {
            try {
                return PromoType.valueOf(value.toUpperCase());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid promo target: " + value);
            }
        }

        @JsonValue
        public String toValue() {
            return name().toLowerCase();
        }
    }
}
