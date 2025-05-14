package com.example.chasier_app_be.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.example.chasier_app_be.dto.UserDTO;
import com.example.chasier_app_be.model.Users;
import com.example.chasier_app_be.repository.UserRepository;
import com.example.chasier_app_be.util.NotFoundException;
import com.example.chasier_app_be.util.ValidationException;

@Service
public class UserService {
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserRepository userRepository;

    private UserDTO convertToDTO(Users user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        return dto;
    };

    public List<UserDTO> getAllUsers() {
        List<Users> users = this.userRepository.findIsActive();
        if (users == null || users.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        return users.stream().map(this::convertToDTO).toList();
    }

    public List<UserDTO> getUserPaginate(int offset, int limit) {
        Query query = new Query()
                .skip(offset)
                .limit(limit)
                .with(Sort.by(Sort.Direction.ASC, "name"));

        List<UserDTO> users = mongoTemplate.find(query, Users.class).stream().map(this::convertToDTO).toList();

        if (users == null || users.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        return users;
    }

    public long countUser() {
        return this.userRepository.count();
    }

    public UserDTO getById(String id) {
        Users user = this.userRepository.findId(id).orElse(null);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return convertToDTO(user);
    }

    public void addUser(Users user) {
        Map<String, String> errors = new HashMap<>();
        List<Users> existingUsers = this.userRepository.findUsername(user.getUsername());
        if (!existingUsers.isEmpty()) {
            errors.put("username", "Username already exists.");
        }
        List<Users> existingEmail = this.userRepository.findEmail(user.getEmail());
        if (!existingEmail.isEmpty()) {
            errors.put("email", "Email already exists.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation error", errors);

        }

        user.setRole("ADMIN");
        user.setCreatedAt(new Date());
        this.userRepository.save(user);
    }

    public void updateUser(String id, Users user) {
        Users existingUser = this.userRepository.findId(id).orElse(null);
        Map<String, String> errors = new HashMap<>();
        List<Users> existingUsername = this.userRepository.findExistingUsername(user.getUsername(), id);
        if (!existingUsername.isEmpty() || existingUser != null) {
            errors.put("username", "Username already exists.");
        }
        List<Users> existingEmail = this.userRepository.findExistingEmail(user.getEmail(), id);
        if (!existingEmail.isEmpty()) {
            errors.put("email", "Email already exists.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation error", errors);
        }

        if (existingUser != null) {
            existingUser.setName(user.getName());
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            existingUser.setUpdatedAt(new Date());
            this.userRepository.save(existingUser);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public void deleteUser(String id) {
        Users existingUser = this.userRepository.findId(id).orElse(null);
        if (existingUser != null) {
            existingUser.setIsActive(false);
            existingUser.setUpdatedAt(new Date());
            this.userRepository.save(existingUser);
        } else {
            throw new NotFoundException("User not found");
        }

    }
}
