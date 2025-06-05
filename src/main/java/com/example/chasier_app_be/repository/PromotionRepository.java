package com.example.chasier_app_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.chasier_app_be.model.Promotion;

public interface PromotionRepository extends MongoRepository<Promotion, String> {

    @Query("{isActive: true}")
    List<Promotion> findIsActive();

    @Query("{'_id' : ?0, isActive: true}")
    Optional<Promotion> findId(String id);

    @Query("{'name' : ?0, isActive: true}")
    List<Promotion> findName(String name);

    @Query("{'name' : ?0, '_id': {$ne :?1} , isActive: true}")
    List<Promotion> findExistingName(String name, String id);
}
