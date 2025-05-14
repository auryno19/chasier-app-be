package com.example.chasier_app_be.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.chasier_app_be.dto.UserDTO;
import com.example.chasier_app_be.model.Users;
import com.example.chasier_app_be.service.UserService;
import com.example.chasier_app_be.util.ApiResponse;
import com.example.chasier_app_be.util.NotFoundException;
import com.example.chasier_app_be.util.ResponseUtil;
import com.example.chasier_app_be.util.ValidationException;

import jakarta.validation.Valid;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/users")
public class ApiUserController {
    @Autowired
    UserService userService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<Object>> getAllUsers() {
        try {
            List<UserDTO> users = this.userService.getAllUsers();
            return ResponseUtil.generateSuccessResponse("Get All Data Success",
                    users,
                    HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseUtil.generateErrorResponse("User not found", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Get data failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getUserById(@PathVariable String id) {
        try {
            UserDTO user = this.userService.getById(id);
            return ResponseUtil.generateSuccessResponse("Get Data Success", user, HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseUtil.generateErrorResponse("User not found", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Get data failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/paginate")
    public ResponseEntity<ApiResponse<Object>> getUserPaginate(
            @RequestParam(defaultValue = "1", name = "page") int page,
            @RequestParam(defaultValue = "5", name = "per_page") int per_page) {
        try {
            int total = (int) this.userService.countUser();
            int total_pages = total % per_page == 0 ? total / per_page : (total / per_page) + 1;
            int offset = (page - 1) * per_page;
            List<UserDTO> users = this.userService.getUserPaginate(offset, per_page);
            Map<String, Object> response = new HashMap<>();
            response.put("page", page);
            response.put("per_page", per_page);
            response.put("total", total);
            response.put("total_pages", total_pages);
            response.put("datas", users);
            return ResponseUtil.generateSuccessResponse("Get Data Success",
                    response,
                    HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseUtil.generateErrorResponse("User not found", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Get data failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Object>> addUser(@Valid @RequestBody Users user) {
        try {
            this.userService.addUser(user);
            return ResponseUtil.generateSuccessResponse("Save Data Success", null, HttpStatus.CREATED);
        } catch (ValidationException e) {
            return ResponseUtil.generateErrorResponse("Validation error", e.getErrors(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Save failed.", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateUser(@PathVariable String id, @Valid @RequestBody Users user) {
        try {
            this.userService.updateUser(id, user);
            return ResponseUtil.generateSuccessResponse("Update Data Success", null, HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseUtil.generateErrorResponse("User not found", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return ResponseUtil.generateErrorResponse("Validation error", e.getErrors(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Update failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable String id) {
        try {
            this.userService.deleteUser(id);
            return ResponseUtil.generateSuccessResponse("Delete Data Success", null, HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseUtil.generateErrorResponse("User not found", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.generateErrorResponse("Delete failed.", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
