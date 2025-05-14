package com.example.chasier_app_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.chasier_app_be.model.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {
    @Query("{'isActive': true}")
    List<Category> findIsActive();

    @Query("{'_id': ?0, 'isActive': true}")
    Optional<Category> findId(String id);

    @Query(value = "{'_id': ?0, 'isActive': true}", fields = "{'name': 1, '_id': 0}")
    Optional<String> getNameById(String id);

    @Query("{'name': { $regex: ?0, $options: 'i' }, 'isActive': true}")
    List<Category> findName(String name);

    @Query("{'name': { $regex: ?0, $options: 'i' }, '_id': {$ne :?1} , 'isActive': true}")
    List<Category> findExistingName(String name, String id);
}
