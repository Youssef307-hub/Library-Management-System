package com.example.libraryManagementSystem.controller;


import com.example.libraryManagementSystem.dto.BorrowingRecordDTO;
import com.example.libraryManagementSystem.exceptionhandling.BadRequestException;
import com.example.libraryManagementSystem.exceptionhandling.DataAlreadyExistException;
import com.example.libraryManagementSystem.exceptionhandling.DataNotFoundException;
import com.example.libraryManagementSystem.model.Book;
import com.example.libraryManagementSystem.model.BorrowingRecord;
import com.example.libraryManagementSystem.model.Customer;
import com.example.libraryManagementSystem.service.BorrowingRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BorrowingRecordsRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class BorrowingRecordsRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BorrowingRecordService recordService;

    private String asJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("TestGetRecords_ReturnRecordsList")
    void testGetRecords_ReturnRecordsList() throws Exception {
        BorrowingRecord record1 = new BorrowingRecord(1L, new Customer(), new Book(), LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 10));
        BorrowingRecord record2 = new BorrowingRecord(2L, new Customer(), new Book(), LocalDate.of(2023, 2, 1), LocalDate.of(2023, 2, 10));
        List<BorrowingRecord> records = Arrays.asList(record1, record2);

        when(recordService.getRecords(anyInt(), anyInt(), anyString()))
                .thenReturn(ResponseEntity.ok(records));

        mockMvc.perform(get("/api/v1/library/borrowings")
                        .param("pageNumber", "0")
                        .param("pageSize", "5")
                        .param("field", "id"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(recordService).getRecords(0, 5, "id");
    }

    @Test
    @DisplayName("TestGetRecords_ThrowDataNotFoundException")
    void testGetRecords_ThrowDataNotFoundException() throws Exception {
        when(recordService.getRecords(anyInt(), anyInt(), anyString()))
                .thenThrow(new DataNotFoundException("No Records Found!"));

        mockMvc.perform(get("/api/v1/library/borrowings"))
                .andExpect(status().isNotFound());

        verify(recordService).getRecords(0, 5, "id");
    }

    @Test
    @DisplayName("TestSearchRecordsByCustomerId_ReturnRecordsList")
    void testSearchRecordsByCustomerId_ReturnRecordsList() throws Exception {
        BorrowingRecord record = new BorrowingRecord(1L, new Customer(), new Book(), LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 10));
        List<BorrowingRecord> records = List.of(record);

        when(recordService.searchRecords(anyLong(), eq(null)))
                .thenReturn(ResponseEntity.ok(records));

        mockMvc.perform(get("/api/v1/library/borrowings/search")
                        .param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(recordService).searchRecords(1L, null);
    }

    @Test
    @DisplayName("TestSearchRecordsByBookId_ReturnRecordsList")
    void testSearchRecordsByBookId_ReturnRecordsList() throws Exception {
        BorrowingRecord record = new BorrowingRecord(1L, new Customer(), new Book(), LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 10));
        List<BorrowingRecord> records = List.of(record);

        when(recordService.searchRecords(eq(null), anyLong()))
                .thenReturn(ResponseEntity.ok(records));

        mockMvc.perform(get("/api/v1/library/borrowings/search")
                        .param("bookId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(recordService).searchRecords(null, 1L);
    }

    @Test
    @DisplayName("TestSearchRecords_ThrowBadRequestException")
    void testSearchRecords_ThrowBadRequestException() throws Exception {

        when(recordService.searchRecords(anyLong(), anyLong()))
                .thenThrow(new BadRequestException("At least one search parameter must be provided."));

        when(recordService.searchRecords(null, null))
                .thenThrow(new BadRequestException("Only one search parameter can be provided at a time."));

        mockMvc.perform(get("/api/v1/library/borrowings/search")
                        .param("customerId", "")
                        .param("bookId", ""))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/library/borrowings/search")
                        .param("customerId", "1")
                        .param("bookId", "1"))
                .andExpect(status().isBadRequest());

        verify(recordService).searchRecords(null, null);
        verify(recordService).searchRecords(1L, 1L);
    }

    @Test
    @DisplayName("TestSearchRecords_ThrowDataNotFoundException")
    void testSearchRecords_ThrowDataNotFoundException() throws Exception {
        when(recordService.searchRecords(eq(null), anyLong()))
                .thenThrow(new DataNotFoundException("No Record Found!"));

        when(recordService.searchRecords(anyLong(), eq(null)))
                .thenThrow(new DataNotFoundException("No Record Found!"));

        mockMvc.perform(get("/api/v1/library/borrowings/search")
                        .param("customerId", "1"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/library/borrowings/search")
                        .param("bookId", "1"))
                .andExpect(status().isNotFound());

        verify(recordService).searchRecords(1L, null);
        verify(recordService).searchRecords(null, 1L);
    }

    @Test
    @DisplayName("TestGetRecordById_ReturnRecord")
    void testGetRecordById_ReturnRecord() throws Exception {
        BorrowingRecord record = new BorrowingRecord(1L, new Customer(), new Book(), LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 10));

        when(recordService.getRecordById(anyLong())).thenReturn(ResponseEntity.ok(record));

        mockMvc.perform(get("/api/v1/library/borrowings/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(recordService).getRecordById(1L);
    }

    @Test
    @DisplayName("TestGetRecordById_ThrowDataNotFoundException")
    void testGetRecordById_ThrowDataNotFoundException() throws Exception {
        when(recordService.getRecordById(anyLong()))
                .thenThrow(new DataNotFoundException("No Record Found!"));

        mockMvc.perform(get("/api/v1/library/borrowings/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(recordService).getRecordById(1L);
    }

    @Test
    @DisplayName("TestAddRecord_ReturnSavedRecord")
    void testAddRecord_ReturnSavedRecord() throws Exception {
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO(1L, 1L, "2023-01-01", "2023-01-10");
        BorrowingRecord record = new BorrowingRecord(1L, new Customer(), new Book(), LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 10));

        when(recordService.addRecord(any(BorrowingRecordDTO.class)))
                .thenReturn(ResponseEntity.ok(record));

        mockMvc.perform(post("/api/v1/library/borrowings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(recordDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(recordService).addRecord(any(BorrowingRecordDTO.class));
    }

    @Test
    @DisplayName("TestAddRecord_ThrowBadRequestException")
    void testAddRecord_ThrowBadRequestException() throws Exception {
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO(1L, 1L, "2023-01-01", "invalid-date");

        mockMvc.perform(post("/api/v1/library/borrowings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(recordDTO)))
                .andExpect(status().isBadRequest());

        verify(recordService, never()).addRecord(any(BorrowingRecordDTO.class));
    }

    @Test
    @DisplayName("TestAddRecord_ThrowDataAlreadyExistException")
    void testAddRecord_ThrowDataAlreadyExistException() throws Exception {
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO(1L, 1L, "2023-01-01", "2023-01-10");

        when(recordService.addRecord(any(BorrowingRecordDTO.class)))
                .thenThrow(new DataAlreadyExistException("This Record Already Exists!"));

        mockMvc.perform(post("/api/v1/library/borrowings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(recordDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("This Record Already Exists!"));

        verify(recordService).addRecord(any(BorrowingRecordDTO.class));
    }

    @Test
    @DisplayName("TestUpdateRecord_ReturnUpdatedRecord")
    void testUpdateRecord_ReturnUpdatedRecord() throws Exception {
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO(1L, 1L, "2023-01-01", "2023-01-10");
        BorrowingRecord record = new BorrowingRecord(1L, new Customer(), new Book(), LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 10));

        when(recordService.updateRecord(anyLong(), any(BorrowingRecordDTO.class)))
                .thenReturn(ResponseEntity.ok(record));

        mockMvc.perform(put("/api/v1/library/borrowings/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(recordDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(recordService).updateRecord(anyLong(), any(BorrowingRecordDTO.class));
    }

    @Test
    @DisplayName("TestUpdateRecord_ThrowBadRequestException")
    void testUpdateRecord_ThrowBadRequestException() throws Exception {
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO(1L, 1L, "2023-01-01", "invalid-date");

        mockMvc.perform(put("/api/v1/library/borrowings/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(recordDTO)))
                .andExpect(status().isBadRequest());

        verify(recordService, never()).updateRecord(anyLong(), any(BorrowingRecordDTO.class));
    }

    @Test
    @DisplayName("TestUpdateRecord_ThrowDataNotFoundException")
    void testUpdateRecord_ThrowDataNotFoundException() throws Exception {
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO(1L, 1L, "2023-01-01", "2023-01-10");

        when(recordService.updateRecord(anyLong(), any(BorrowingRecordDTO.class)))
                .thenThrow(new DataNotFoundException("No Borrowing Record With The ID: 1 Found!"));

        mockMvc.perform(put("/api/v1/library/borrowings/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(recordDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No Borrowing Record With The ID: 1 Found!"));

        verify(recordService).updateRecord(anyLong(), any(BorrowingRecordDTO.class));
    }

    @Test
    @DisplayName("TestDeleteRecord_ReturnSuccessMessage")
    void testDeleteRecord_ReturnSuccessMessage() throws Exception {
        when(recordService.deleteRecord(anyLong()))
                .thenReturn(ResponseEntity.ok("Borrowing record deleted successfully"));

        mockMvc.perform(delete("/api/v1/library/borrowings/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Borrowing record deleted successfully"));

        verify(recordService).deleteRecord(anyLong());
    }

    @Test
    @DisplayName("TestDeleteRecord_ThrowDataNotFoundException")
    void testDeleteRecord_ThrowDataNotFoundException() throws Exception {
        when(recordService.deleteRecord(anyLong()))
                .thenThrow(new DataNotFoundException("No Borrowing Record With The ID: 1 Found!"));

        mockMvc.perform(delete("/api/v1/library/borrowings/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No Borrowing Record With The ID: 1 Found!"));

        verify(recordService).deleteRecord(anyLong());
    }
}