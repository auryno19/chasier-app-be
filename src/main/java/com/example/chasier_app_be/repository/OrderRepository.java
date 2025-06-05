package com.example.chasier_app_be.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.chasier_app_be.model.Order;

public interface OrderRepository extends MongoRepository<Order, String> {

    @Query("{ 'userId' : ?0 }")
    Optional<Order> findById(String id);
}
