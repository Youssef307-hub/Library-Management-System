package com.example.libraryManagementSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "BorrowingRecord")
@Table(name = "borrowing_record")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRecord {

    @Id
    @SequenceGenerator(
            sequenceName = "record_sequence",
            name = "record_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "record_sequence",
            strategy = GenerationType.SEQUENCE
    )
    @Column(
            name = "id",
            updatable = false,
            nullable = false,
            columnDefinition = "BIGINT"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "customer_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "customer_id_fk")
    )
    private Customer customer;

    @ManyToOne
    @JoinColumn(
            name = "book_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "book_id_fk")
    )
    private Book book;

    @Column(
            name = "borrow_date",
            columnDefinition = "DATE",
            nullable = false
    )
    private LocalDate borrowDate;

    @Column(
            name = "return_date",
            columnDefinition = "DATE",
            nullable = false
    )
    private LocalDate returnDate;
}
