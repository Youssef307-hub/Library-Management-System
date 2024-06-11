package com.example.libraryManagementSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "Book")
@Table(name = "book")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @SequenceGenerator(
            sequenceName = "book_sequence",
            name = "book_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "book_sequence",
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
            name = "title",
            columnDefinition = "VARCHAR(255)"
    )
    @NotNull
    private String title;

    @Column(
            name = "publication_date",
            columnDefinition = "VARCHAR(255)"
    )
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in the format YYYY-MM-DD")
    private String publicationDate;

    @Column(
            name = "isbn",
            columnDefinition = "VARCHAR(255)",
            nullable = false
    )
    @NotNull
    private String isbn;

    @Column(
            name = "genre",
            columnDefinition = "VARCHAR(255)"
    )
    private String genre;

    @Column(
            name = "available",
            columnDefinition = "BOOLEAN",
            nullable = false
    )
    @NotNull
    private boolean available;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "author_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "author_id_fk")
    )
    private Author author;
}
