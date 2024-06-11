package com.example.libraryManagementSystem.controller;

import com.example.libraryManagementSystem.model.Author;
import com.example.libraryManagementSystem.service.AuthorService;
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
@RequestMapping("/api/v1/library/authors")
@RequiredArgsConstructor
public class AuthorRestController {

    private final AuthorService authorService;



    @Operation(summary = "Get all authors", description = "Retrieve all authors paginated and sorted by a specified field", tags = "Authors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of authors retrieved successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Author.class)) }),
            @ApiResponse(responseCode = "404", description = "No authors found")
    })
    @GetMapping
    public ResponseEntity<List<Author>> getAuthors(
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "id", required = false) String field) {
        return authorService.getAuthors(pageNumber, pageSize, field);
    }



    @Operation(summary = "Get author by ID", description = "Retrieve an author by its unique ID", tags = "Authors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Author.class)) }),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        return authorService.getAuthorById(id);
    }



    @Operation(summary = "Add a new author", description = "Add a new author to the system", tags = "Authors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author added successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Author.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "409", description = "Author already exists")
    })
    @PostMapping
    public ResponseEntity<Author> addAuthor(@Valid @RequestBody Author author) {
        return authorService.addAuthor(author);
    }



    @Operation(summary = "Update an existing author", description = "Update details of an existing author", tags = "Authors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author updated successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Author.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable Long id, @Valid @RequestBody Author author) {
        return authorService.updateAuthor(id, author);
    }



    @Operation(summary = "Delete an author", description = "Delete an author from the system", tags = "Authors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuthor(@PathVariable Long id) {
        return authorService.deleteAuthor(id);
    }

}
