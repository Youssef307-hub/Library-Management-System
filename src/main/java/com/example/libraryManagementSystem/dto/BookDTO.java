package com.example.libraryManagementSystem.dto;

import com.example.libraryManagementSystem.model.Author;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    @NotNull
    private String title;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in the format YYYY-MM-DD")
    private String publicationDate;

    @NotNull
    private String isbn;

    private String genre;

    @NotNull
    private boolean available;

    @NotNull
    private Author author;
}
