package com.example.libraryManagementSystem.repository;

import com.example.libraryManagementSystem.model.Author;
import com.example.libraryManagementSystem.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByTitleAndIsbn(String title, String isbn);

    List<Book> findByTitle(String title);

    List<Book> findByAuthorName(String authorName);

    List<Book> findByIsbn(String isbn);
}
