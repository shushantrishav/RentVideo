package com.rentvideo.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String role; // Optional, defaults to CUSTOMER if null
}
