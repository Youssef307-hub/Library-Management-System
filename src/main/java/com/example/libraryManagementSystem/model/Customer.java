package com.example.libraryManagementSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "Customer")
@Table(name = "customer", uniqueConstraints = {
        @UniqueConstraint(name = "email_unique_key", columnNames = "email"),
        @UniqueConstraint(name = "phone_unique_key", columnNames = "phone_number")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @SequenceGenerator(
            sequenceName = "customer_sequence",
            name = "customer_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "customer_sequence",
            strategy = GenerationType.SEQUENCE
    )
    @Column(
            name = "id",
            updatable = false,
            nullable = false,
            columnDefinition = "BIGINT"
    )
    private Long id;

    @Column(
            name = "name",
            nullable = false,
            columnDefinition = "VARCHAR(255)"
    )
    @NotBlank
    private String name;

    @Column(
            name = "email",
            nullable = false,
            columnDefinition = "VARCHAR(255)"
    )
    @NotBlank
    @Email
    private String email;

    @Column(
            name = "phone_number",
            columnDefinition = "VARCHAR(255)"
    )
    @Pattern(
            regexp = "^(010|011|012|015)\\d{8}$",
            message = "Phone number must be a valid Egyptian phone number starting with 010, 011, 012, or 015 followed by 8 digits."
    )
    private String phoneNumber;

    @Column(
            name = "address",
            columnDefinition = "VARCHAR(255)"
    )
    private String address;

    @Column(
            name = "password",
            columnDefinition = "VARCHAR(60)",
            nullable = false
    )
    @NotBlank
    @Size(min = 8, max = 64)
    private String password;


}
