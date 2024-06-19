package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.dto.BookDTO;
import com.example.libraryManagementSystem.exceptionhandling.BadRequestException;
import com.example.libraryManagementSystem.exceptionhandling.DataAlreadyExistException;
import com.example.libraryManagementSystem.exceptionhandling.DataNotFoundException;
import com.example.libraryManagementSystem.model.Author;
import com.example.libraryManagementSystem.model.Book;
import com.example.libraryManagementSystem.repository.AuthorRepository;
import com.example.libraryManagementSystem.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Cacheable("books")
    public ResponseEntity<List<Book>> getBooks(int pageNumber, int pageSize, String field) {

        if (bookRepository.findAll().isEmpty())
            throw new DataNotFoundException("No Books Found!");

        if (pageNumber <= 0)
            pageNumber = 0;

        if (pageSize <= 0)
            pageSize = 5;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(field));

        Page<Book> booksPage = bookRepository.findAll(pageable);

        return new ResponseEntity<>(booksPage.getContent(), HttpStatus.OK);
    }

    @Cacheable("books")
    public ResponseEntity<List<Book>> searchBooks(String title, String isbn, String authorName) {
        int nonNullParamsCount = 0;
        if (title != null)
            nonNullParamsCount++;
        if (isbn != null)
            nonNullParamsCount++;
        if (authorName != null)
            nonNullParamsCount++;

        if (nonNullParamsCount == 0) {
            throw new BadRequestException("At least one search parameter must be provided.");
        } else if (nonNullParamsCount > 1) {
            throw new BadRequestException("Only one search parameter can be provided at a time.");
        }

        if (bookRepository.findAll().isEmpty())
            throw new DataNotFoundException("No Books Found!");

        if (title != null) {
            return getBooksByTitle(title);
        } else if (isbn != null) {
            return getBooksByIsbn(isbn);
        } else {
            return getBooksByAuthor(authorName);
        }
    }

    public ResponseEntity<List<Book>> getBooksByTitle(String title) {
        if (bookRepository.findByTitle(title).isEmpty())
            throw new DataNotFoundException("No Books Found!");

        return new ResponseEntity<>(bookRepository.findByTitle(title), HttpStatus.OK);
    }

    public ResponseEntity<List<Book>> getBooksByIsbn(String isbn) {
        if (bookRepository.findByIsbn(isbn).isEmpty())
            throw new DataNotFoundException("No Books Found!");

        return new ResponseEntity<>(bookRepository.findByIsbn(isbn), HttpStatus.OK);
    }

    public ResponseEntity<List<Book>> getBooksByAuthor(String authorName) {
        if (bookRepository.findByAuthorName(authorName).isEmpty())
            throw new DataNotFoundException("No Books Found!");

        return new ResponseEntity<>(bookRepository.findByAuthorName(authorName), HttpStatus.OK);
    }

    @Cacheable("books")
    public ResponseEntity<Book> getBookById(Long id) {
        if (bookRepository.findById(id).isEmpty())
            throw new DataNotFoundException("No Book With The ID: " + id + " Found!");

        return new ResponseEntity<>(bookRepository.findById(id).get(), HttpStatus.OK);
    }

    @CacheEvict(value = "books", allEntries = true)
    public ResponseEntity<Book> addBook(BookDTO bookDTO) {
        if (bookRepository.existsByTitleAndIsbn(bookDTO.getTitle(), bookDTO.getIsbn()))
            throw new DataAlreadyExistException("This Book Already Exists!");

        // Check if the author exists in the database
        Author author = authorRepository.findByNameAndBirthDateAndNationality(
                bookDTO.getAuthor().getName(),
                bookDTO.getAuthor().getBirthDate(),
                bookDTO.getAuthor().getNationality()
        ).orElseGet(() -> {
            // If the author does not exist, save the new author
            Author newAuthor = Author.builder()
                    .name(bookDTO.getAuthor().getName())
                    .birthDate(bookDTO.getAuthor().getBirthDate())
                    .nationality(bookDTO.getAuthor().getNationality())
                    .build();
            return authorRepository.save(newAuthor);
        });

        Book newBook = Book
                .builder()
                .title(bookDTO.getTitle())
                .genre(bookDTO.getGenre())
                .isbn(bookDTO.getIsbn())
                .publicationDate(LocalDate.parse(bookDTO.getPublicationDate()))
                .available(bookDTO.isAvailable())
                .author(author)
                .build();

        return new ResponseEntity<>(bookRepository.save(newBook), HttpStatus.OK);
    }

    @CacheEvict(value = "books", allEntries = true)
    public ResponseEntity<Book> updateBook(Long id, BookDTO bookDTO) {
        if (bookRepository.findById(id).isEmpty())
            throw new DataNotFoundException("No Book With The ID: " + id + " Found!");

        // Check if the author exists in the database
        Author author = authorRepository.findByNameAndBirthDateAndNationality(
                bookDTO.getAuthor().getName(),
                bookDTO.getAuthor().getBirthDate(),
                bookDTO.getAuthor().getNationality()
        ).orElseGet(() -> {
            // If the author does not exist, save the new author
            Author newAuthor = Author.builder()
                    .name(bookDTO.getAuthor().getName())
                    .birthDate(bookDTO.getAuthor().getBirthDate())
                    .nationality(bookDTO.getAuthor().getNationality())
                    .build();
            return authorRepository.save(newAuthor);
        });

        Book updatedBook = bookRepository.findById(id).get();
        updatedBook.setTitle(bookDTO.getTitle());
        updatedBook.setIsbn(bookDTO.getIsbn());
        updatedBook.setGenre(bookDTO.getGenre());
        updatedBook.setAvailable(bookDTO.isAvailable());
        updatedBook.setAuthor(author);
        updatedBook.setPublicationDate(LocalDate.parse(bookDTO.getPublicationDate()));

        return new ResponseEntity<>(bookRepository.save(updatedBook), HttpStatus.OK);
    }

    @CacheEvict(value = "books", allEntries = true)
    public ResponseEntity<String> deleteBook(Long id) {
        if (bookRepository.findById(id).isEmpty())
            throw new DataNotFoundException("No Book With The ID: " + id + " Found!");

        bookRepository.deleteById(id);

        return new ResponseEntity<>("Book With ID: " + id + " Deleted Successfully!", HttpStatus.OK);
    }
}
