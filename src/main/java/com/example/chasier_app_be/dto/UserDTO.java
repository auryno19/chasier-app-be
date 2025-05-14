package com.example.chasier_app_be.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String role;
}
