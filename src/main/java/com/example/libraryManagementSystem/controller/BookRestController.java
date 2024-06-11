package com.example.libraryManagementSystem.controller;

import com.example.libraryManagementSystem.dto.BookDTO;
import com.example.libraryManagementSystem.model.Book;
import com.example.libraryManagementSystem.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/library/books")
public class BookRestController {

    private final BookService bookService;


    @Operation(summary = "Get all books", description = "Retrieve all books paginated and sorted by a specified field", tags = {"Books"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of books retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))})
    })
    @GetMapping
    public ResponseEntity<List<Book>> getBooks(
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "id", required = false) String field) {
        return bookService.getBooks(pageNumber, pageSize, field);
    }


    @Operation(summary = "Search books", description = "Search for books by title, ISBN, or author name", tags = {"Books"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Books not found")
    })
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String authorName) {
        return bookService.searchBooks(title, isbn, authorName);
    }


    @Operation(summary = "Get book by ID", description = "Retrieve a book by its unique ID", tags = {"Books"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }


    @Operation(summary = "Add a new book", description = "Add a new book to the library. If the author does not exist, a new author will be created.", tags = {"Books"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book added successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "409", description = "Book already exists")
    })
    @PostMapping
    public ResponseEntity<Book> addBook(@Valid @RequestBody BookDTO bookDTO) {
        return bookService.addBook(bookDTO);
    }


    @Operation(summary = "Update an existing book", description = "Update details of an existing book", tags = {"Books"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookDTO bookDTO) {
        return bookService.updateBook(id, bookDTO);
    }


    @Operation(summary = "Delete a book", description = "Delete a book from the system", tags = {"Books"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        return bookService.deleteBook(id);
    }
}
