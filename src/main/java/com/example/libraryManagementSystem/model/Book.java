package com.example.libraryManagementSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private String title;

    @Column(
            name = "publication_date",
            columnDefinition = "DATE"
    )
    private LocalDate publicationDate;

    @Column(
            name = "isbn",
            columnDefinition = "VARCHAR(255)",
            nullable = false
    )
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
    private boolean available;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(
            name = "author_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "author_id_fk")
    )
    private Author author;
}
