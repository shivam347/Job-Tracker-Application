package com.java.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* DTO stands for data transfer objects used to carry data between the processes
 * example between client and server or between different layers of the applications
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @Email
    @NotBlank
    @Size(max = 50)
    private String email;

    @NotBlank
    private String password;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;
    
}
