package com.example.libraryManagementSystem.repository;

import com.example.libraryManagementSystem.model.Book;
import com.example.libraryManagementSystem.model.BorrowingRecord;
import com.example.libraryManagementSystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {
    boolean existsByBookAndCustomer(Book book, Customer customer);

    List<BorrowingRecord> findByBook(Book book);

    List<BorrowingRecord> findByCustomer(Customer customer);

}
