package com.java.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class LoginRequest {
    
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(max = 50)
    private String password;
}
