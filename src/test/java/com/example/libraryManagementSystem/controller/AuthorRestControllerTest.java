package com.example.libraryManagementSystem.controller;

import com.example.libraryManagementSystem.dto.AuthorDTO;
import com.example.libraryManagementSystem.exceptionhandling.DataAlreadyExistException;
import com.example.libraryManagementSystem.exceptionhandling.DataNotFoundException;
import com.example.libraryManagementSystem.model.Author;
import com.example.libraryManagementSystem.service.AuthorService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthorRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AuthorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

    private String asJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("TestGetAuthors_ReturnAuthorsList")
    void testGetAuthors_ReturnAuthorsList() throws Exception {
        List<Author> authors = Arrays.asList(
                new Author(1L, "Author 1", LocalDate.of(1970, 1, 1), "American"),
                new Author(2L, "Author 2", LocalDate.of(1980, 2, 2), "British")
        );

        when(authorService.getAuthors(anyInt(), anyInt(), anyString()))
                .thenReturn(ResponseEntity.ok(authors));

        mockMvc.perform(get("/api/v1/library/authors")
                        .param("pageNumber", "0")
                        .param("pageSize", "5")
                        .param("field", "id"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Author 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Author 2"));

        verify(authorService).getAuthors(0, 5, "id");
    }

    @Test
    @DisplayName("TestGetAuthors_ThrowDataNotFoundException")
    void testGetAuthors_ThrowDataNotFoundException() throws Exception {
        when(authorService.getAuthors(anyInt(), anyInt(), anyString()))
                .thenThrow(new DataNotFoundException("No Authors Found!"));

        mockMvc.perform(get("/api/v1/library/authors"))
                .andExpect(status().isNotFound());

        verify(authorService).getAuthors(0, 5, "id");
    }

    @Test
    @DisplayName("TestGetAuthorById_ReturnAuthor")
    void testGetAuthorById_ReturnAuthor() throws Exception {
        Author author = new Author(1L, "Author 1", LocalDate.of(1970, 1, 1), "American");

        when(authorService.getAuthorById(1L))
                .thenReturn(ResponseEntity.ok(author));

        mockMvc.perform(get("/api/v1/library/authors/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Author 1"));

        verify(authorService).getAuthorById(1L);
    }

    @Test
    @DisplayName("TestGetAuthorById_ThrowDataNotFoundException")
    void testGetAuthorById_ThrowDataNotFoundException() throws Exception {

        when(authorService.getAuthorById(1L))
                .thenThrow(new DataNotFoundException("No Author with ID: " + 1L + " found"));

        mockMvc.perform(get("/api/v1/library/authors/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(authorService).getAuthorById(1L);
    }

    @Test
    @DisplayName("TestAddAuthor_ReturnSavedAuthor")
    void testAddAuthor_ReturnSavedAuthor() throws Exception {
        AuthorDTO authorDTO = new AuthorDTO("Author", "1980-05-15", "American");
        Author savedAuthor = new Author(1L, "Author", LocalDate.of(1980, 5, 15), "American");

        when(authorService.addAuthor(any(AuthorDTO.class)))
                .thenReturn(new ResponseEntity<>(savedAuthor, HttpStatus.OK));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/library/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(authorDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Author"))
                .andExpect(jsonPath("$.birthDate").value("1980-05-15"))
                .andExpect(jsonPath("$.nationality").value("American"));
    }

    @Test
    @DisplayName("TestAddAuthor_ThrowBadRequestException")
    void testAddAuthor_ThrowBadRequestException() throws Exception {
        // Mock data with invalid authorDTO (missing name, wrong date, blank nationality)
        AuthorDTO authorDTO = new AuthorDTO(null, "1985-8-20", " ");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/library/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(authorDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("TestAddAuthor_ThrowDataAlreadyExistException")
    void testAddAuthor_ThrowDataAlreadyExistException() throws Exception {
        AuthorDTO authorDTO = new AuthorDTO("Author", "1985-08-20", "British");

        when(authorService.addAuthor(any(AuthorDTO.class)))
                .thenThrow(new DataAlreadyExistException("This Author Already Exists!"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/library/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(authorDTO)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    @DisplayName("TestUpdateAuthor_ReturnUpdatedAuthor")
    void testUpdateAuthor_ReturnUpdatedAuthor() throws Exception {
        Long authorId = 1L;
        AuthorDTO authorDTO = new AuthorDTO("Author", "1985-08-20", "British");
        Author updatedAuthor = new Author(authorId, "Author", LocalDate.of(1985, 8, 20), "British");

        when(authorService.updateAuthor(eq(authorId), any(AuthorDTO.class)))
                .thenReturn(new ResponseEntity<>(updatedAuthor, HttpStatus.OK));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/library/authors/{id}", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(authorDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Author"))
                .andExpect(jsonPath("$.birthDate").value("1985-08-20"))
                .andExpect(jsonPath("$.nationality").value("British"));
    }

    @Test
    @DisplayName("TestUpdateAuthor_ThrowBadRequestException")
    void testUpdateAuthor_ThrowBadRequestException() throws Exception {
        // Mock data with invalid authorDTO (missing name, wrong date, blank nationality)
        Long authorId = 1L;
        AuthorDTO authorDTO = new AuthorDTO(null, "1985-8-20", " ");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/library/authors/{id}", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(authorDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("TestUpdateAuthor_ThrowDataNotFoundException")
    void testUpdateAuthor_ThrowDataNotFoundException() throws Exception {
        Long authorId = 999L;
        AuthorDTO authorDTO = new AuthorDTO("Author", "1985-08-20", "British");

        when(authorService.updateAuthor(eq(authorId), any(AuthorDTO.class)))
                .thenThrow(new DataNotFoundException("No Author With The ID: " + authorId + " Found!"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/library/authors/{id}", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(authorDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("TestDeleteAuthor_ReturnSuccessMessage")
    void testDeleteAuthor_ReturnSuccessMessage() throws Exception {
        Long authorId = 1L;

        when(authorService.deleteAuthor(eq(authorId)))
                .thenReturn(new ResponseEntity<>(anyString(), HttpStatus.OK));


        mockMvc.perform(delete("/api/v1/library/authors/{id}", authorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("TestDeleteAuthor_ThrowDataNotFoundException")
    void testDeleteAuthor_ThrowDataNotFoundException() throws Exception {
        Long authorId = 1L;

        when(authorService.deleteAuthor(eq(authorId)))
                .thenThrow(new DataNotFoundException("No Author With The ID: " + authorId + " Found!"));


        mockMvc.perform(delete("/api/v1/library/authors/{id}", authorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}