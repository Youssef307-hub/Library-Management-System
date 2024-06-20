package com.example.libraryManagementSystem.controller;

import com.example.libraryManagementSystem.dto.BookDTO;
import com.example.libraryManagementSystem.exceptionhandling.BadRequestException;
import com.example.libraryManagementSystem.exceptionhandling.DataAlreadyExistException;
import com.example.libraryManagementSystem.exceptionhandling.DataNotFoundException;
import com.example.libraryManagementSystem.model.Author;
import com.example.libraryManagementSystem.model.Book;
import com.example.libraryManagementSystem.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private String asJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("TestGetBooks_ReturnBooksList")
    void testGetBooks_ReturnBooksList() throws Exception {
        List<Book> books = Arrays.asList(
                new Book(1L, "Book 1", LocalDate.now(),"ISBN1", "Genre 1", true, new Author()),
                new Book(2L, "Book 2", LocalDate.now(),"ISBN2", "Genre 2", true, new Author())
        );

        when(bookService.getBooks(anyInt(), anyInt(), anyString()))
                .thenReturn(ResponseEntity.ok(books));

        mockMvc.perform(get("/api/v1/library/books")
                        .param("pageNumber", "0")
                        .param("pageSize", "5")
                        .param("field", "id"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Book 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Book 2"));

        verify(bookService).getBooks(0, 5, "id");
    }

    @Test
    @DisplayName("TestGetBooks_ThrowDataNotFoundException")
    void testGetBooks_ThrowDataNotFoundException() throws Exception {
        when(bookService.getBooks(anyInt(), anyInt(), anyString()))
                .thenThrow(new DataNotFoundException("No Authors Found!"));

        mockMvc.perform(get("/api/v1/library/books"))
                .andExpect(status().isNotFound());

        verify(bookService).getBooks(0, 5, "id");
    }

    @Test
    @DisplayName("TestSearchBooksByTitle_ReturnBooksList")
    void testSearchBooksByTitle_ReturnBooksList() throws Exception {
        List<Book> books = List.of(
                new Book(1L, "Book 1", LocalDate.now(), "ISBN1", "Genre 1", true, new Author())
        );

        when(bookService.searchBooks("Book 1", null, null))
                .thenReturn(ResponseEntity.ok(books));

        mockMvc.perform(get("/api/v1/library/books/search")
                        .param("title", "Book 1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Book 1"));

        verify(bookService).searchBooks("Book 1", null, null);
    }

    @Test
    @DisplayName("TestSearchBooksByISBN_ReturnBooksList")
    void testSearchBooksByISBN_ReturnBooksList() throws Exception {
        List<Book> books = List.of(
                new Book(1L, "Book 1", LocalDate.now(), "ISBN1", "Genre 1", true, new Author())
        );

        when(bookService.searchBooks(null, "ISBN1", null))
                .thenReturn(ResponseEntity.ok(books));

        mockMvc.perform(get("/api/v1/library/books/search")
                        .param("isbn", "ISBN1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].isbn").value("ISBN1"));

        verify(bookService).searchBooks(null, "ISBN1", null);
    }

    @Test
    @DisplayName("TestSearchBooksByAuthorName_ReturnBooksList")
    void testSearchBooksByAuthorName_ReturnBooksList() throws Exception {
        Author author = new Author(1L, "Author", LocalDate.of(1980, 5, 15), "American");
        List<Book> books = List.of(
                new Book(1L, "Book 1", LocalDate.now(), "ISBN1", "Genre 1", true, author)
        );

        when(bookService.searchBooks(null, null, "Author"))
                .thenReturn(ResponseEntity.ok(books));

        mockMvc.perform(get("/api/v1/library/books/search")
                        .param("authorName", "Author"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].author.name").value("Author"));

        verify(bookService).searchBooks(null, null, "Author");
    }

    @Test
    @DisplayName("TestSearchBooks_ThrowDataNotFoundException")
    void testSearchBooks_ThrowDataNotFoundException() throws Exception {
        when(bookService.searchBooks("Not Found Book", null, null))
                .thenThrow(new DataNotFoundException("No Books Found!"));

        when(bookService.searchBooks(null, "Not Found ISBN", null))
                .thenThrow(new DataNotFoundException("No Books Found!"));

        when(bookService.searchBooks(null, null, "Not Found Author"))
                .thenThrow(new DataNotFoundException("No Books Found!"));

        mockMvc.perform(get("/api/v1/library/books/search")
                        .param("title", "Not Found Book"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/library/books/search")
                        .param("isbn", "Not Found ISBN"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/library/books/search")
                        .param("authorName", "Not Found Author"))
                .andExpect(status().isNotFound());

        verify(bookService).searchBooks("Not Found Book", null, null);
        verify(bookService).searchBooks(null, "Not Found ISBN", null);
        verify(bookService).searchBooks(null, null, "Not Found Author");
    }

    @Test
    @DisplayName("TestSearchBooks_ThrowBadRequestException")
    void testSearchBooks_ThrowBadRequestException() throws Exception {
        when(bookService.searchBooks("", "", ""))
                .thenThrow(new BadRequestException("At least one search parameter must be provided."));

        when(bookService.searchBooks("title", "ISBN", "Author"))
                .thenThrow(new BadRequestException("Only one search parameter can be provided at a time."));

        when(bookService.searchBooks("title", "ISBN", ""))
                .thenThrow(new BadRequestException("Only one search parameter can be provided at a time."));

        when(bookService.searchBooks("title", "", "Author"))
                .thenThrow(new BadRequestException("Only one search parameter can be provided at a time."));

        when(bookService.searchBooks("", "ISBN", "Author"))
                .thenThrow(new BadRequestException("Only one search parameter can be provided at a time."));

        mockMvc.perform(get("/api/v1/library/books/search")
                        .param("title", "")
                        .param("isbn", "")
                        .param("authorName", ""))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/library/books/search")
                        .param("title", "title")
                        .param("isbn", "ISBN")
                        .param("authorName", "Author"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/library/books/search")
                        .param("title", "title")
                        .param("isbn", "")
                        .param("authorName", "Author"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/library/books/search")
                        .param("title", "")
                        .param("isbn", "ISBN")
                        .param("authorName", "Author"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/library/books/search")
                        .param("title", "title")
                        .param("isbn", "ISBN")
                        .param("authorName", ""))
                .andExpect(status().isBadRequest());

        verify(bookService).searchBooks("", "", "");
        verify(bookService).searchBooks("title", "ISBN", "Author");
        verify(bookService).searchBooks("title", "ISBN", "");
        verify(bookService).searchBooks("title", "", "Author");
        verify(bookService).searchBooks("", "ISBN", "Author");
    }

    @Test
    @DisplayName("TestGetBookById_ReturnBook")
    void testGetBookById_ReturnBook() throws Exception {
        Book book = new Book(1L, "Book 1", LocalDate.now(),"ISBN1", "Genre 1", true, new Author());

        when(bookService.getBookById(1L))
                .thenReturn(ResponseEntity.ok(book));

        mockMvc.perform(get("/api/v1/library/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Book 1"))
                .andExpect(jsonPath("$.genre").value("Genre 1"))
                .andExpect(jsonPath("$.isbn").value("ISBN1"));

        verify(bookService).getBookById(1L);
    }

    @Test
    @DisplayName("TestGetBookById_ThrowDataNotFoundException")
    void testGetBookById_ThrowDataNotFoundException() throws Exception {
        when(bookService.getBookById(anyLong()))
                .thenThrow(new DataNotFoundException("No Book with ID: " + 1L + " found"));

        mockMvc.perform(get("/api/v1/library/books/{id}", 2L))
                .andExpect(status().isNotFound());

        verify(bookService).getBookById(2L);
    }

    @Test
    @DisplayName("TestAddBook_ReturnSavedBook")
    void testAddBook_ReturnSavedBook() throws Exception {
        BookDTO bookDTO = new BookDTO("Book","2023-03-03","ISBN1", "Genre1"  , true, new Author());

        Book savedBook = new Book(1L, "Book", LocalDate.of(2023,3,3), "ISBN1", "Genre1" , true, new Author());

        when(bookService.addBook(any(BookDTO.class)))
                .thenReturn(ResponseEntity.ok(savedBook));

        mockMvc.perform(post("/api/v1/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Book"))
                .andExpect(jsonPath("$.genre").value("Genre1"))
                .andExpect(jsonPath("$.isbn").value("ISBN1"));

        verify(bookService).addBook(any(BookDTO.class));
    }

    @Test
    @DisplayName("TestAddBook_ThrowBadRequestException")
    void testAddBook_ThrowBadRequestException() throws Exception {
        // Mock data with invalid bookDto missing required fields
        BookDTO bookDTO = new BookDTO();

        mockMvc.perform(post("/api/v1/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TestAddBook_ThrowDataAlreadyExistException")
    void testAddBook_ThrowDataAlreadyExistException() throws Exception {
        BookDTO bookDTO = new BookDTO("Book","2023-03-03","ISBN1", "Genre1"  , true, new Author());

        when(bookService.addBook(any(BookDTO.class)))
                .thenThrow(new DataAlreadyExistException("Book already exists"));

        mockMvc.perform(post("/api/v1/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookDTO)))
                .andExpect(status().isConflict());

        verify(bookService).addBook(any(BookDTO.class));
    }

    @Test
    @DisplayName("TestUpdateBook_ReturnUpdatedBook")
    void testUpdateBook_ReturnUpdatedBook() throws Exception {
        Long bookId = 1L;
        BookDTO bookDTO = new BookDTO("Book", "2023-03-03", "ISBN1", "Genre1", true, new Author());

        Book savedBook = new Book(1L, "Book", LocalDate.of(2023, 3, 3), "ISBN1", "Genre1", true, new Author());

        when(bookService.updateBook(bookId, bookDTO))
                .thenReturn(ResponseEntity.ok(savedBook));

        mockMvc.perform(put("/api/v1/library/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Book"))
                .andExpect(jsonPath("$.publicationDate").value("2023-03-03"))
                .andExpect(jsonPath("$.isbn").value("ISBN1"))
                .andExpect(jsonPath("$.genre").value("Genre1"))
                .andExpect(jsonPath("$.available").value(true));

        verify(bookService).updateBook(bookId, bookDTO);
    }

    @Test
    @DisplayName("TestUpdateBook_ThrowBadRequestException")
    void testUpdateBook_ThrowBadRequestException() throws Exception {
        Long bookId = 1L;

        // Invalid: missing required fields
        BookDTO bookDTO = new BookDTO();

        mockMvc.perform(put("/api/v1/library/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TestUpdateBook_ThrowDataNotFoundException")
    void testUpdateBook_ThrowDataNotFoundException() throws Exception {
        Long bookId = 999L;
        BookDTO bookDTO = new BookDTO("Book", "2023-03-03", "ISBN1", "Genre1", true, new Author());

        when(bookService.updateBook(bookId, bookDTO))
                .thenThrow(new DataNotFoundException("Book not found"));

        mockMvc.perform(put("/api/v1/library/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookDTO)))
                .andExpect(status().isNotFound());

        verify(bookService).updateBook(bookId, bookDTO);
    }

    @Test
    @DisplayName("TestDeleteBook_ReturnSuccessMessage")
    void testDeleteBook_ReturnSuccessMessage() throws Exception {
        Long bookId = 1L;

        when(bookService.deleteBook(eq(bookId)))
                .thenReturn(new ResponseEntity<>(anyString(), HttpStatus.OK));


        mockMvc.perform(delete("/api/v1/library/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("TestDeleteBook_ThrowDataNotFoundException")
    void testDeleteBook_ThrowDataNotFoundException() throws Exception {
        Long bookId = 1L;

        when(bookService.deleteBook(eq(bookId)))
                .thenThrow(new DataNotFoundException("No Author With The ID: " + bookId + " Found!"));


        mockMvc.perform(delete("/api/v1/library/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}