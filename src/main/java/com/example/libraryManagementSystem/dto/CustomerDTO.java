package com.example.libraryManagementSystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @Pattern(
            regexp = "^(010|011|012|015)\\d{8}$",
            message = "Phone number must be a valid Egyptian phone number starting with 010, 011, 012, or 015 followed by 8 digits."
    )
    @NotNull
    private String phoneNumber;

    private String address;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character."
    )
    @NotNull
    private String password;
}
