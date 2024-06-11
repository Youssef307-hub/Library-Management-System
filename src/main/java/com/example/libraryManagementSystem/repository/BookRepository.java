package com.example.libraryManagementSystem.repository;

import com.example.libraryManagementSystem.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByTitleAndIsbn(String title, String isbn);

    List<Book> findByTitle(String title);

    List<Book> findByAuthorName(String authorName);

    List<Book> findByIsbn(String isbn);
}
