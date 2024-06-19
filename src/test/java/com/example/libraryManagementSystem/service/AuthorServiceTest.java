package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.dto.AuthorDTO;
import com.example.libraryManagementSystem.exceptionhandling.DataAlreadyExistException;
import com.example.libraryManagementSystem.exceptionhandling.DataNotFoundException;
import com.example.libraryManagementSystem.model.Author;
import com.example.libraryManagementSystem.repository.AuthorRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository repository;

    private AuthorService service;

    @BeforeEach
    void setUp() {
        service = new AuthorService(repository);
    }

    @AfterEach
    void tearDown() {
        service = null;
    }

    @Test
    @DisplayName("TestGetAuthors_ReturnAuthorsList")
    void testGetAuthors_ReturnAuthorsList() {
        List<Author> authors = Arrays.asList(
                new Author(1L, "Author 1", LocalDate.of(1970, 1, 1), "American"),
                new Author(2L, "Author 2", LocalDate.of(1980, 2, 2), "British")
        );

        Page<Author> authorPage = new PageImpl<>(authors);

        when(repository.findAll()).thenReturn(authors);
        when(repository.findAll(any(Pageable.class))).thenReturn(authorPage);

        ResponseEntity<List<Author>> response = service.getAuthors(0, 5, "id");

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertIterableEquals(authors, response.getBody());
        assertEquals(2, response.getBody().size());

        // Check the contents of the body is what we expected
        assertEquals("Author 1", response.getBody().get(0).getName());
        assertEquals(LocalDate.of(1970, 1, 1), response.getBody().get(0).getBirthDate());
        assertEquals("American", response.getBody().get(0).getNationality());

        assertEquals("Author 2", response.getBody().get(1).getName());
        assertEquals(LocalDate.of(1980, 2, 2), response.getBody().get(1).getBirthDate());
        assertEquals("British", response.getBody().get(1).getNationality());
    }

    @Test
    @DisplayName("TestGetAuthors_ThrowDataNotFoundException")
    void testGetAuthors_ThrowDataNotFoundException() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(DataNotFoundException.class, () -> {
            service.getAuthors(0, 5, "id");
        });
    }

    @Test
    @DisplayName("TestGetAuthors_ReturnAuthorsList_InvalidPaginationValues")
    void testGetAuthors_ReturnAuthorsList_InvalidPaginationValues() {
        List<Author> authors = Arrays.asList(
                new Author(1L, "Author 1", LocalDate.of(1970, 1, 1), "American"),
                new Author(2L, "Author 2", LocalDate.of(1980, 2, 2), "British")
        );
        int pageNumber = -1;
        int pageSize = 0;
        String field = "Wrong";

        Page<Author> authorPage = new PageImpl<>(authors);

        when(repository.findAll()).thenReturn(authors);
        when(repository.findAll(any(Pageable.class))).thenReturn(authorPage);

        ResponseEntity<List<Author>> response = service.getAuthors(pageNumber, pageSize, field);

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertIterableEquals(authors, response.getBody());
        assertEquals(2, response.getBody().size());

        // Check the contents of the body is what we expected
        assertEquals("Author 1", response.getBody().get(0).getName());
        assertEquals(LocalDate.of(1970, 1, 1), response.getBody().get(0).getBirthDate());
        assertEquals("American", response.getBody().get(0).getNationality());

        assertEquals("Author 2", response.getBody().get(1).getName());
        assertEquals(LocalDate.of(1980, 2, 2), response.getBody().get(1).getBirthDate());
        assertEquals("British", response.getBody().get(1).getNationality());


    }

    @Test
    @DisplayName("TestGetAuthorById_ReturnAuthor")
    void testGetAuthorById_ReturnAuthor() {
        Author author = new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American");
        when(repository.findById(author.getId())).thenReturn(Optional.of(author));

        ResponseEntity<Author> response = service.getAuthorById(1L);

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(author, response.getBody());

        // Check the contents of the body is what we expected
        assertEquals("Author", response.getBody().getName());
        assertEquals(LocalDate.of(1970, 1, 1), response.getBody().getBirthDate());
        assertEquals("American", response.getBody().getNationality());
    }

    @Test
    @DisplayName("TestGetAuthorById_ThrowDataNotFoundException")
    void testGetAuthorById_ThrowDataNotFoundException() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Long id = 1L;

        assertThrows(DataNotFoundException.class, () -> {
            service.getAuthorById(id);
        });
    }

    @Test
    @DisplayName("TestAddAuthor_ReturnSavedAuthor")
    void testAddAuthor_ReturnSavedAuthor() {
        AuthorDTO authorDTO = new AuthorDTO("Author", "1970-01-01", "American");
        Author author = Author.builder()
                .name(authorDTO.getName())
                .birthDate(LocalDate.parse(authorDTO.getBirthDate()))
                .nationality(authorDTO.getNationality())
                .build();
        when(repository.existsByNameAndBirthDateAndNationality(
                author.getName(), LocalDate.parse(authorDTO.getBirthDate()), authorDTO.getNationality())).thenReturn(false);
        when(repository.save(author)).thenReturn(author);

        ResponseEntity<Author> response = service.addAuthor(authorDTO);

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(author, response.getBody());

        // Check the contents of the body is what we expected
        assertEquals("Author", response.getBody().getName());
        assertEquals(LocalDate.of(1970, 1, 1), response.getBody().getBirthDate());
        assertEquals("American", response.getBody().getNationality());

    }

    @Test
    @DisplayName("TestAddAuthor_ThrowDataNotFoundException")
    void testAddAuthor_ThrowDataAlreadyExistException() {
        AuthorDTO authorDTO = new AuthorDTO("Author", "1970-01-01", "American");
        when(repository.existsByNameAndBirthDateAndNationality(
                authorDTO.getName(), LocalDate.parse(authorDTO.getBirthDate()), authorDTO.getNationality())).thenReturn(true);

        assertThrows(DataAlreadyExistException.class, () -> {
            service.addAuthor(authorDTO);
        });
    }

    @Test
    @DisplayName("TestUpdateAuthor_ReturnUpdatedAuthor")
    void testUpdateAuthor_ReturnUpdatedAuthor() {
        Long authorId = 1L;
        AuthorDTO authorDTO = new AuthorDTO("Updated", "1980-01-01", "British");

        Author existingAuthor = new Author(authorId, "Original", LocalDate.of(1970, 1, 1), "American");

        when(repository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(repository.save(any(Author.class))).thenReturn(existingAuthor);

        ResponseEntity<Author> response = service.updateAuthor(authorId, authorDTO);

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existingAuthor, response.getBody());

        // Check the contents of the body is what we expected
        assertEquals("Updated", response.getBody().getName());
        assertEquals(LocalDate.of(1980, 1, 1), response.getBody().getBirthDate());
        assertEquals("British", response.getBody().getNationality());
    }

    @Test
    @DisplayName("TestUpdateAuthor_ThrowDataNotFoundException")
    void testUpdateAuthor_ThrowDataNotFoundException() {
        Long authorId = 1L;
        AuthorDTO authorDTO = new AuthorDTO("Updated Author", "1980-01-01", "British");

        when(repository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            service.updateAuthor(authorId, authorDTO);
        });
    }

    @Test
    @DisplayName("TestDeleteAuthor_ReturnSuccessMessage")
    void testDeleteAuthor_ReturnSuccessMessage() {
        Author author = new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American");
        when(repository.findById(author.getId())).thenReturn(Optional.of(author));

        ResponseEntity<String> response = service.deleteAuthor(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(repository, times(1)).findById(author.getId());
        verify(repository, times(1)).deleteById(author.getId());
    }

    @Test
    @DisplayName("TestDeleteAuthor_ThrowDataNotFoundException")
    void testDeleteAuthor_ThrowDataNotFoundException() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Long id = 1L;

        assertThrows(DataNotFoundException.class, () -> {
            service.deleteAuthor(id);
        });
    }
}