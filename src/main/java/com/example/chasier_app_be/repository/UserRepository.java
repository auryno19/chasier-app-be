package com.example.chasier_app_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.chasier_app_be.model.Users;

public interface UserRepository extends MongoRepository<Users, String> {
    @Query("{'isActive': true}")
    List<Users> findIsActive();

    @Query("{'_id': ?0, 'isActive': true}")
    Optional<Users> findId(String id);

    @Query("{'username': { $regex: ?0, $options: 'i' }, 'isActive': true}")
    List<Users> findUsername(String username);

    @Query("{'email': { $regex: ?0, $options: 'i' }, 'isActive': true'}")
    List<Users> findEmail(String email);

    @Query("{'username': { $regex: ?0, $options: 'i' }, '_id': {$ne :?1} , 'isActive': true}")
    List<Users> findExistingUsername(String username, String id);

    @Query("{'email': { $regex: ?0, $options: 'i' }, '_id': {$ne :?1} , 'isActive': true}")
    List<Users> findExistingEmail(String email, String id);
}
