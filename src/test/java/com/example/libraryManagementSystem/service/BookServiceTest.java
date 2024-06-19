package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.dto.BookDTO;
import com.example.libraryManagementSystem.exceptionhandling.BadRequestException;
import com.example.libraryManagementSystem.exceptionhandling.DataAlreadyExistException;
import com.example.libraryManagementSystem.exceptionhandling.DataNotFoundException;
import com.example.libraryManagementSystem.model.Author;
import com.example.libraryManagementSystem.model.Book;
import com.example.libraryManagementSystem.repository.AuthorRepository;
import com.example.libraryManagementSystem.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    private BookService service;

    @BeforeEach
    void setUp() {
        service = new BookService(bookRepository, authorRepository);
    }

    @AfterEach
    void tearDown() {
        service = null;
    }

    @Test
    @DisplayName("TestGetBooks_ReturnBooksList")
    void testGetBooks_ReturnBooksList() {
        Author author = new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American");

        List<Book> books = Arrays.asList(
                new Book(1L, "Book", LocalDate.of(2023, 6, 18), "1234567890", "Fiction", true, author),
                new Book(2L, "Book2", LocalDate.of(2022, 7, 20), "1234567800", "Fiction", true, author)
        );
        Page<Book> booksPage = new PageImpl<>(books);
        when(bookRepository.findAll()).thenReturn(books);
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(booksPage);

        ResponseEntity<List<Book>> response = service.getBooks(0, 5, "id");

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertIterableEquals(books, response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    @DisplayName("TestGetBooks_ReturnBooksList_InvalidPaginationValues")
    void testGetBooks_ReturnBooksList_InvalidPaginationValues() {
        Author author = new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American");

        List<Book> books = Arrays.asList(
                new Book(1L, "Book", LocalDate.of(2023, 6, 18), "1234567890", "Fiction", true, author),
                new Book(2L, "Book2", LocalDate.of(2022, 7, 20), "1234567800", "Fiction", true, author)
        );

        int pageNumber = -1;
        int pageSize = 0;
        String field = "Wrong";

        Page<Book> booksPage = new PageImpl<>(books);
        when(bookRepository.findAll()).thenReturn(books);
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(booksPage);

        ResponseEntity<List<Book>> response = service.getBooks(pageNumber, pageSize, field);

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertIterableEquals(books, response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    @DisplayName("TestGetBooks_ThrowDataNotFoundException")
    void testGetBooks_ThrowDataNotFoundException() {

        when(bookRepository.findAll()).thenReturn(List.of());

        assertThrows(DataNotFoundException.class, () -> {
            service.getBooks(0, 5, "title");
        });
    }

    @Test
    @DisplayName("TestSearchBooks_ThrowBadRequestException")
    void testSearchBooks_ThrowBadRequestException() {
        assertThrows(BadRequestException.class, () -> {
            service.searchBooks(null, null, null);
        });

        assertThrows(BadRequestException.class, () -> {
            service.searchBooks("title", "isbn", null);
        });

        assertThrows(BadRequestException.class, () -> {
            service.searchBooks(null, "isbn", "authorName");
        });

        assertThrows(BadRequestException.class, () -> {
            service.searchBooks("title", null, "authorName");
        });

        assertThrows(BadRequestException.class, () -> {
            service.searchBooks("title", "isbn", "authorName");
        });
    }

    @Test
    @DisplayName("TestSearchBooks_ThrowDataNotFoundException")
    void testSearchBooks_ThrowDataNotFoundException() {
        when(bookRepository.findAll()).thenReturn(List.of());

        assertThrows(DataNotFoundException.class, () -> {
            service.searchBooks("title", null, null);
        });

        assertThrows(DataNotFoundException.class, () -> {
            service.searchBooks(null, "isbn", null);
        });

        assertThrows(DataNotFoundException.class, () -> {
            service.searchBooks(null, null, "authorName");
        });
    }

    @Test
    @DisplayName("TestSearchBooksByTitle_ReturnBooksList")
    void testSearchBooksByTitle_ReturnBooksList() {
        String title = "Book";
        Author author = new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American");
        Book book = new Book(1L, "Book", LocalDate.of(2023, 6, 18), "1234567890", "Fiction", true, author);

        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(bookRepository.findByTitle(title)).thenReturn(List.of(book));

        ResponseEntity<List<Book>> response = service.searchBooks(title, null, null);

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertIterableEquals(List.of(book), response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("TestSearchBooksByTitle_ThrowDataNotFoundException")
    void testSearchBooksByTitle_ThrowDataNotFoundException() {
        String title = "NotFoundBook";
        when(bookRepository.findByTitle(title)).thenReturn(List.of());

        assertThrows(DataNotFoundException.class, () -> {
            service.getBooksByTitle(title);
        });
    }

    @Test
    @DisplayName("TestSearchBooksByIsbn_ReturnBooksList")
    void testSearchBooksByIsbn_ReturnBooksList() {
        String isbn = "1234567890";
        Author author = new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American");
        Book book = new Book(1L, "Book", LocalDate.of(2023, 6, 18), "1234567890", "Fiction", true, author);

        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(bookRepository.findByIsbn(isbn)).thenReturn(List.of(book));

        ResponseEntity<List<Book>> response = service.searchBooks(null, isbn, null);

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertIterableEquals(List.of(book), response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("TestSearchBooksByIsbn_ThrowDataNotFoundException")
    void testSearchBooksByIsbn_ThrowDataNotFoundException() {
        String isbn = "123";
        when(bookRepository.findByIsbn(isbn)).thenReturn(List.of());

        assertThrows(DataNotFoundException.class, () -> {
            service.getBooksByIsbn(isbn);
        });
    }

    @Test
    @DisplayName("TestSearchBooksByAuthorName_ReturnBooksList")
    void testSearchBooksByAuthorName_ReturnBooksList() {
        String authorName = "Author";
        Author author = new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American");
        Book book = new Book(1L, "Book", LocalDate.of(2023, 6, 18), "1234567890", "Fiction", true, author);

        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(bookRepository.findByAuthorName(authorName)).thenReturn(List.of(book));

        ResponseEntity<List<Book>> response = service.searchBooks(null, null, authorName);

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertIterableEquals(List.of(book), response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("TestSearchBooksByAuthorName_ThrowDataNotFoundException")
    void testSearchBooksByAuthorName_ThrowDataNotFoundException() {
        String authorName = "NotFoundAuthor";
        when(bookRepository.findByAuthorName(authorName)).thenReturn(List.of());

        assertThrows(DataNotFoundException.class, () -> {
            service.getBooksByAuthor(authorName);
        });
    }

    @Test
    @DisplayName("TestGetBookById_ReturnBook")
    void testGetBookById_ReturnBook() {
        Author author = new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American");
        Book book = new Book(1L, "Book", LocalDate.of(2023, 6, 18), "1234567890", "Fiction", true, author);
        when(bookRepository.findById(author.getId())).thenReturn(Optional.of(book));

        ResponseEntity<Book> response = service.getBookById(1L);

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(book, response.getBody());
    }

    @Test
    @DisplayName("TestGetBookById_ThrowDataNotFoundException")
    void testGetBookById_ThrowDataNotFoundException() {
        Long id = 1L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            service.getBookById(id);
        });
    }

    @Test
    @DisplayName("TestAddBook_ReturnSavedAuthor_AuthorExist")
    void testAddBook_ReturnSavedAuthor_AuthorExist() {
        Author author = new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American");
        BookDTO bookDTO = new BookDTO("Book", "2023-06-18", "1234567890", "Fiction", true, author);

        Book newBook = Book
                .builder()
                .title(bookDTO.getTitle())
                .genre(bookDTO.getGenre())
                .isbn(bookDTO.getIsbn())
                .publicationDate(LocalDate.parse(bookDTO.getPublicationDate()))
                .available(bookDTO.isAvailable())
                .author(author)
                .build();

        when(bookRepository.existsByTitleAndIsbn(bookDTO.getTitle(), bookDTO.getIsbn())).thenReturn(false);
        when(authorRepository.findByNameAndBirthDateAndNationality(
                bookDTO.getAuthor().getName(),
                bookDTO.getAuthor().getBirthDate(),
                bookDTO.getAuthor().getNationality()))
                .thenReturn(Optional.of(author));
        when(bookRepository.save(newBook)).thenReturn(newBook);

        ResponseEntity<Book> response = service.addBook(bookDTO);

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newBook, response.getBody());
    }

    @Test
    @DisplayName("TestAddBook_ReturnSavedAuthor_AuthorDoesNotExist")
    void testAddBook_ReturnSavedAuthor_AuthorDoesNotExist() {
        BookDTO bookDTO = new BookDTO("Book", "2023-06-18", "1234567890", "Fiction", true,
                new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American"));

        Author newAuthor = Author.builder()
                .name(bookDTO.getAuthor().getName())
                .birthDate(bookDTO.getAuthor().getBirthDate())
                .nationality(bookDTO.getAuthor().getNationality())
                .build();

        Book newBook = Book
                .builder()
                .title(bookDTO.getTitle())
                .genre(bookDTO.getGenre())
                .isbn(bookDTO.getIsbn())
                .publicationDate(LocalDate.parse(bookDTO.getPublicationDate()))
                .available(bookDTO.isAvailable())
                .author(newAuthor)
                .build();

        when(bookRepository.existsByTitleAndIsbn(bookDTO.getTitle(), bookDTO.getIsbn())).thenReturn(false);
        when(authorRepository.findByNameAndBirthDateAndNationality(
                bookDTO.getAuthor().getName(),
                bookDTO.getAuthor().getBirthDate(),
                bookDTO.getAuthor().getNationality()))
                .thenReturn(Optional.empty());
        when(authorRepository.save(newAuthor)).thenReturn(newAuthor);
        when(bookRepository.save(newBook)).thenReturn(newBook);

        ResponseEntity<Book> response = service.addBook(bookDTO);

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newBook, response.getBody());
    }

    @Test
    @DisplayName("TestAddBook_ThrowDataAlreadyExistException")
    void testAddBook_ThrowsDataAlreadyExistException() {
        BookDTO bookDTO = new BookDTO("Book", "1234567890", "Fiction", "2023-06-18", true,
                new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American"));

        when(bookRepository.existsByTitleAndIsbn(bookDTO.getTitle(), bookDTO.getIsbn())).thenReturn(true);

        assertThrows(DataAlreadyExistException.class, () -> {
            service.addBook(bookDTO);
        });
    }

    @Test
    @DisplayName("TestUpdateBook_ReturnUpdatedBook_AuthorExist")
    void testUpdateBook_ReturnUpdatedBook_AuthorExist() {
        Long bookId = 1L;
        Author author = new Author(1L, "NewAuthor", LocalDate.of(1970, 1, 1), "American");
        BookDTO bookDTO = new BookDTO("UpdatedBook", "2023-06-18", "1234567890", "Fiction", true,
                author);

        Book updatedBook = Book
                .builder()
                .id(bookId)
                .title(bookDTO.getTitle())
                .genre(bookDTO.getGenre())
                .isbn(bookDTO.getIsbn())
                .publicationDate(LocalDate.parse(bookDTO.getPublicationDate()))
                .available(bookDTO.isAvailable())
                .author(author)
                .build();

        Book existingBook = new Book(1L, "Existing", LocalDate.parse("2022-01-01"), "0987654321", "Non-fiction"
                , true, new Author(1L, "oldAuthor", LocalDate.of(1980, 5, 15), "British"));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(authorRepository.findByNameAndBirthDateAndNationality(
                bookDTO.getAuthor().getName(),
                bookDTO.getAuthor().getBirthDate(),
                bookDTO.getAuthor().getNationality()))
                .thenReturn(Optional.of(updatedBook.getAuthor()));
        when(bookRepository.save(updatedBook)).thenReturn(updatedBook);

        ResponseEntity<Book> response = service.updateBook(bookId, bookDTO);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existingBook, updatedBook);
        assertEquals(updatedBook, response.getBody());
    }

    @Test
    @DisplayName("TestUpdateBook_ReturnUpdatedBook_AuthorDoesNotExist")
    void testUpdateBook_ReturnUpdatedBook_AuthorDoesNotExist() {
        Long bookId = 1L;
        BookDTO bookDTO = new BookDTO("UpdatedBook", "2023-06-18", "1234567890", "Fiction", true,
                new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American"));

        Author newAuthor = Author.builder()
                .name(bookDTO.getAuthor().getName())
                .birthDate(bookDTO.getAuthor().getBirthDate())
                .nationality(bookDTO.getAuthor().getNationality())
                .build();

        Book updatedBook = Book
                .builder()
                .id(bookId)
                .title(bookDTO.getTitle())
                .genre(bookDTO.getGenre())
                .isbn(bookDTO.getIsbn())
                .publicationDate(LocalDate.parse(bookDTO.getPublicationDate()))
                .available(bookDTO.isAvailable())
                .author(newAuthor)
                .build();

        Book existingBook = new Book(1L, "Existing", LocalDate.parse("2022-01-01"), "0987654321", "Non-fiction"
                , true, new Author(1L, "oldAuthor", LocalDate.of(1980, 5, 15), "British"));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(authorRepository.findByNameAndBirthDateAndNationality(
                bookDTO.getAuthor().getName(),
                bookDTO.getAuthor().getBirthDate(),
                bookDTO.getAuthor().getNationality()))
                .thenReturn(Optional.empty());
        when(authorRepository.save(newAuthor)).thenReturn(newAuthor);
        when(bookRepository.save(updatedBook)).thenReturn(updatedBook);

        ResponseEntity<Book> response = service.updateBook(bookId, bookDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existingBook, updatedBook);
        assertEquals(updatedBook, response.getBody());
    }

    @Test
    @DisplayName("TestUpdateBook_ThrowDataNotFoundException")
    void testUpdateBook_ThrowDataNotFoundException() {
        Long bookId = 999L;
        BookDTO bookDTO = new BookDTO("Book", "1234567890", "Fiction", "2023-06-18", true,
                new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American"));

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            service.updateBook(bookId, bookDTO);
        });
    }

    @Test
    @DisplayName("TestDeleteBook_ReturnSuccessMessage")
    void testDeleteBook_ReturnSuccessMessage() {
        Long bookId = 1L;
        Book book = new Book(1L, "Book", LocalDate.of(2023, 6, 18), "1234567890", "Fiction", true,
                new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American"));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        ResponseEntity<String> response = service.deleteBook(bookId);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    @DisplayName("TestDeleteBook_ThrowDataNotFoundException")
    void testDeleteBook_ThrowDataNotFoundException() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        Long id = 1L;

        assertThrows(DataNotFoundException.class, () -> {
            service.deleteBook(id);
        });
    }

}