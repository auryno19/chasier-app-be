package com.example.chasier_app_be.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.chasier_app_be.model.DailyOrderCounter;

public interface DailyOrderCounterRepository extends MongoRepository<DailyOrderCounter, String> {

}
