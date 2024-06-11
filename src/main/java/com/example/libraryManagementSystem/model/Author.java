package com.example.libraryManagementSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity(name = "Author")
@Table(name = "author")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Author {

    @Id
    @SequenceGenerator(
            sequenceName = "author_sequence",
            name = "author_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "author_sequence",
            strategy = GenerationType.SEQUENCE)
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
            name = "birth_date",
            columnDefinition = "VARCHAR(255)"
    )
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in the format YYYY-MM-DD")
    private String birthDate;

    @Column(
            name = "nationality",
            columnDefinition = "VARCHAR(255)"
    )
    private String nationality;
}
