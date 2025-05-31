package com.example.chasier_app_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.chasier_app_be.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
    @Query("{'isActive': true}")
    List<Product> findIsActive();

    @Query("{'_id': ?0, 'isActive': true}")
    Optional<Product> findId(String id);

    @Query("{'name': ?0, 'isActive': true}")
    List<Product> findName(String name);

    @Query("{'name': ?0, '_id': {$ne :?1} , 'isActive': true}")
    List<Product> findExistingName(String name, String id);

    @Query("{'categoryId': ?0, 'isActive': true}")
    List<Product> findCategoryId(String categoryId);
}
