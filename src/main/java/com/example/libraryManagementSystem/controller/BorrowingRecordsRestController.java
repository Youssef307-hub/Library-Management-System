package com.example.libraryManagementSystem.controller;

import com.example.libraryManagementSystem.dto.BorrowingRecordDTO;
import com.example.libraryManagementSystem.model.BorrowingRecord;
import com.example.libraryManagementSystem.service.BorrowingRecordService;
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
@RequestMapping("/api/v1/library/borrowings")
@RequiredArgsConstructor
public class BorrowingRecordsRestController {

    private final BorrowingRecordService recordService;


    @Operation(summary = "Get all borrowing records", description = "Retrieve all borrowing records with pagination and sorting", tags = {"Borrowing Records"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of borrowing records retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BorrowingRecord.class))}),
            @ApiResponse(responseCode = "404", description = "No borrowing records found")
    })
    @GetMapping
    public ResponseEntity<List<BorrowingRecord>> getRecords(
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "id", required = false) String field) {
        return recordService.getRecords(pageNumber, pageSize, field);
    }


    @Operation(summary = "Search borrowing records", description = "Search for borrowing records by customer ID or book ID", tags = {"Borrowing Records"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrowing records found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BorrowingRecord.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "No borrowing records found")
    })
    @GetMapping("/search")
    public ResponseEntity<List<BorrowingRecord>> searchRecords(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long bookId) {
        return recordService.searchRecords(customerId, bookId);
    }


    @Operation(summary = "Get borrowing record by ID", description = "Retrieve a borrowing record by its ID", tags = {"Borrowing Records"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrowing record retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BorrowingRecord.class))}),
            @ApiResponse(responseCode = "404", description = "Borrowing record not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BorrowingRecord> getRecordById(@PathVariable Long id) {
        return recordService.getRecordById(id);
    }

    @Operation(summary = "Add a new borrowing record", description = "Add a new borrowing record", tags = {"Borrowing Records"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrowing record added successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BorrowingRecordDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "409", description = "Record already exists")
    })
    @PostMapping
    public ResponseEntity<BorrowingRecord> addRecord(@Valid @RequestBody BorrowingRecordDTO recordDTO) {
        return recordService.addRecord(recordDTO);
    }

    @Operation(summary = "Update a borrowing record", description = "Update a borrowing record by its ID", tags = {"Borrowing Records"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrowing record updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BorrowingRecordDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Borrowing record not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BorrowingRecord> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody BorrowingRecordDTO recordDTO) {
        return recordService.updateRecord(id, recordDTO);
    }

    @Operation(summary = "Delete a borrowing record", description = "Delete a borrowing record by its ID", tags = {"Borrowing Records"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrowing record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Borrowing record not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecord(@PathVariable Long id) {
        return recordService.deleteRecord(id);
    }
}
