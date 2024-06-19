package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.dto.BorrowingRecordDTO;
import com.example.libraryManagementSystem.exceptionhandling.BadRequestException;
import com.example.libraryManagementSystem.exceptionhandling.DataAlreadyExistException;
import com.example.libraryManagementSystem.exceptionhandling.DataNotFoundException;
import com.example.libraryManagementSystem.model.Author;
import com.example.libraryManagementSystem.model.Book;
import com.example.libraryManagementSystem.model.BorrowingRecord;
import com.example.libraryManagementSystem.model.Customer;
import com.example.libraryManagementSystem.repository.BookRepository;
import com.example.libraryManagementSystem.repository.BorrowingRecordRepository;
import com.example.libraryManagementSystem.repository.CustomerRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowingRecordServiceTest {

    @Mock
    private BorrowingRecordRepository recordRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CustomerRepository customerRepository;

    private BorrowingRecordService service;

    @BeforeEach
    void setUp() {
        service = new BorrowingRecordService(recordRepository, bookRepository, customerRepository);
    }

    @AfterEach
    void tearDown() {
        service = null;
    }

    @Test
    @DisplayName("TestGetRecords_ReturnRecordsList")
    void testGetRecords_ReturnRecordsList() {
        Book book = new Book(1L, "Book", LocalDate.of(2023, 6, 18), "1234567890", "Fiction", true,
                new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American"));
        Customer customer = new Customer(1L, "Customer", "customer@example.com", "010101010101", "123 Street", "Abc123456");
        List<BorrowingRecord> records = List.of(
                new BorrowingRecord(1L, customer, book, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 1))
        );
        Page<BorrowingRecord> recordsPage = new PageImpl<>(records);
        when(recordRepository.findAll()).thenReturn(records);
        when(recordRepository.findAll(any(Pageable.class))).thenReturn(recordsPage);

        ResponseEntity<List<BorrowingRecord>> response = service.getRecords(0, 5, "id");

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertIterableEquals(records, response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("TestGetRecords_ReturnRecordsList_InvalidPaginationValues")
    void testGetRecords_ReturnRecordsList_InvalidPaginationValues() {
        Book book = new Book(1L, "Book", LocalDate.of(2023, 6, 18), "1234567890", "Fiction", true,
                new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American"));
        Customer customer = new Customer(1L, "Customer", "customer@example.com", "010101010101", "123 Street", "Abc123456");
        List<BorrowingRecord> records = List.of(
                new BorrowingRecord(1L, customer, book, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 1))
        );

        int pageNumber = -1;
        int pageSize = 0;
        String field = "Wrong";

        Page<BorrowingRecord> recordsPage = new PageImpl<>(records);
        when(recordRepository.findAll()).thenReturn(records);
        when(recordRepository.findAll(any(Pageable.class))).thenReturn(recordsPage);

        ResponseEntity<List<BorrowingRecord>> response = service.getRecords(pageNumber, pageSize, field);

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertIterableEquals(records, response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("TestGetRecords_ThrowDataNotFoundException")
    void testGetRecords_ThrowDataNotFoundException() {

        when(recordRepository.findAll()).thenReturn(List.of());

        assertThrows(DataNotFoundException.class, () -> {
            service.getRecords(0, 5, "id");
        });
    }

    @Test
    @DisplayName("TestSearchRecords_ThrowBadRequestException")
    void testSearchRecords_ThrowBadRequestException() {
        assertThrows(BadRequestException.class, () -> {
            service.searchRecords(null, null);
        });

        assertThrows(BadRequestException.class, () -> {
            service.searchRecords(1L, 1L);
        });
    }

    @Test
    @DisplayName("TestSearchRecordsByBook_ReturnRecordsList")
    void testSearchRecordsByBook_ReturnRecordsList() {
        Long bookId = 1L;
        Book book = new Book(1L, "Book", LocalDate.of(2023, 6, 18), "1234567890", "Fiction", true,
                new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American"));
        Customer customer = new Customer(1L, "Customer", "customer@example.com", "010101010101", "123 Street", "Abc123456");
        BorrowingRecord record = new BorrowingRecord(1L, customer, book, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 1));

        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(recordRepository.findByBook(book)).thenReturn(List.of(record));

        ResponseEntity<List<BorrowingRecord>> response = service.searchRecords(null, bookId);

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertIterableEquals(List.of(record), response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("TestSearchRecordsByCustomer_ReturnRecordsList")
    void testSearchRecordsByCustomer_ReturnRecordsList() {
        Long customerId = 1L;
        Book book = new Book(1L, "Book", LocalDate.of(2023, 6, 18), "1234567890", "Fiction", true,
                new Author(1L, "Author", LocalDate.of(1970, 1, 1), "American"));
        Customer customer = new Customer(1L, "Customer", "customer@example.com", "010101010101", "123 Street", "Abc123456");
        BorrowingRecord record = new BorrowingRecord(1L, customer, book, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 1));

        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(recordRepository.findByCustomer(customer)).thenReturn(List.of(record));

        ResponseEntity<List<BorrowingRecord>> response = service.searchRecords(customerId, null);

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertIterableEquals(List.of(record), response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("TestSearchRecordsByBook_BookExistsButNoRecordsFound_ThrowDataNotFoundException")
    void testSearchRecordsByBook_BookExistsButNoRecordsFound_ThrowDataNotFoundException() {
        Long bookId = 999L;
        Book book = new Book();

        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(recordRepository.findByBook(book)).thenReturn(List.of());

        assertThrows(DataNotFoundException.class, () -> {
            service.searchRecords(null, bookId);
        });

    }

    @Test
    @DisplayName("TestSearchRecordsByBook_BookNotExists_ThrowDataNotFoundException")
    void testSearchRecordsByBook_BookNotExists_ThrowDataNotFoundException() {
        Long bookId = 999L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            service.searchRecords(null, bookId);
        });
    }

    @Test
    @DisplayName("TestSearchRecordsByCustomer_CustomerExistsButNoRecordsFound_ThrowDataNotFoundException")
    void testSearchRecordsByCustomer_CustomerExistsButNoRecordsFound_ThrowDataNotFoundException() {
        Long customerId = 999L;
        Customer customer = new Customer();

        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(recordRepository.findByCustomer(customer)).thenReturn(List.of());

        assertThrows(DataNotFoundException.class, () -> {
            service.searchRecords(customerId, null);
        });

    }

    @Test
    @DisplayName("TestSearchRecordsByCustomer_CustomerNotExists_ThrowDataNotFoundException")
    void testSearchRecordsByCustomer_CustomerNotExists_ThrowDataNotFoundException() {
        Long customerId = 999L;

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            service.searchRecords(customerId, null);
        });
    }

    @Test
    @DisplayName("TestGetRecordById_ReturnRecord")
    void testGetRecordById_ReturnRecord() {
        Long recordId = 1L;
        BorrowingRecord record = new BorrowingRecord();
        when(recordRepository.findById(recordId)).thenReturn(Optional.of(record));

        ResponseEntity<BorrowingRecord> response = service.getRecordById(recordId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(record, response.getBody());
    }

    @Test
    @DisplayName("TestGetRecordById_ThrowDataNotFoundException")
    void testGetRecordById_ThrowDataNotFoundException() {
        Long recordId = 1L;
        when(recordRepository.findById(recordId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            service.getRecordById(recordId);
        });
    }

    @Test
    @DisplayName("TestAddRecord_BookNotFound_ThrowDataNotFoundException")
    void testAddRecord_BookNotFound_ThrowDataNotFoundException() {
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO();
        recordDTO.setBookId(1L);

        when(bookRepository.findById(recordDTO.getBookId())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            service.addRecord(recordDTO);
        });
        verify(bookRepository, times(1)).findById(recordDTO.getBookId());
    }

    @Test
    @DisplayName("TestAddRecord_BookNotAvailable_ThrowBadRequestException")
    void testAddRecord_BookNotAvailable_ThrowBadRequestException() {
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO();
        recordDTO.setBookId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setAvailable(false);

        when(bookRepository.findById(recordDTO.getBookId())).thenReturn(Optional.of(book));

        assertThrows(BadRequestException.class, () -> {
            service.addRecord(recordDTO);
        });
        verify(bookRepository, times(1)).findById(recordDTO.getBookId());
    }

    @Test
    @DisplayName("TestAddRecord_CustomerNotFound_ThrowDataNotFoundException")
    void testAddRecord_CustomerNotFound_ThrowDataNotFoundException() {
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO();
        recordDTO.setBookId(1L);
        recordDTO.setCustomerId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setAvailable(true);

        when(bookRepository.findById(recordDTO.getBookId())).thenReturn(Optional.of(book));
        when(customerRepository.findById(recordDTO.getCustomerId())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            service.addRecord(recordDTO);
        });
        verify(bookRepository, times(1)).findById(recordDTO.getBookId());
        verify(customerRepository, times(1)).findById(recordDTO.getCustomerId());
    }

    @Test
    @DisplayName("TestAddRecord_RecordAlreadyExists_ThrowDataAlreadyExistException")
    void testAddRecord_RecordAlreadyExists_ThrowDataAlreadyExistException() {
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO();
        recordDTO.setBookId(1L);
        recordDTO.setCustomerId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setAvailable(true);

        Customer customer = new Customer();
        customer.setId(1L);

        when(bookRepository.findById(recordDTO.getBookId())).thenReturn(Optional.of(book));
        when(customerRepository.findById(recordDTO.getCustomerId())).thenReturn(Optional.of(customer));
        when(recordRepository.existsByBookAndCustomer(book, customer)).thenReturn(true);

        assertThrows(DataAlreadyExistException.class, () -> {
            service.addRecord(recordDTO);
        });
        verify(bookRepository, times(1)).findById(recordDTO.getBookId());
        verify(customerRepository, times(1)).findById(recordDTO.getCustomerId());
        verify(recordRepository, times(1)).existsByBookAndCustomer(book, customer);
    }

    @Test
    @DisplayName("TestAddRecord_InvalidDates_ThrowBadRequestException")
    void testAddRecord_InvalidDates_ThrowBadRequestException() {
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO();
        recordDTO.setBookId(1L);
        recordDTO.setCustomerId(1L);
        recordDTO.setBorrowDate("2023-06-10");
        recordDTO.setReturnDate("2023-06-01");

        Book book = new Book();
        book.setId(1L);
        book.setAvailable(true);

        Customer customer = new Customer();
        customer.setId(1L);

        when(bookRepository.findById(recordDTO.getBookId())).thenReturn(Optional.of(book));
        when(customerRepository.findById(recordDTO.getCustomerId())).thenReturn(Optional.of(customer));
        when(recordRepository.existsByBookAndCustomer(book, customer)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> {
            service.addRecord(recordDTO);
        });
        verify(bookRepository, times(1)).findById(recordDTO.getBookId());
        verify(customerRepository, times(1)).findById(recordDTO.getCustomerId());
        verify(recordRepository, times(0)).save(any(BorrowingRecord.class)); // No save should happen
    }

    @Test
    @DisplayName("TestAddRecord_ReturnRecord")
    void testAddRecord_ReturnRecord() {
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO();
        recordDTO.setBookId(1L);
        recordDTO.setCustomerId(1L);
        recordDTO.setBorrowDate("2023-06-01");
        recordDTO.setReturnDate("2023-06-10");

        Book book = new Book();
        book.setId(1L);
        book.setAvailable(true);

        Customer customer = new Customer();
        customer.setId(1L);

        when(bookRepository.findById(recordDTO.getBookId())).thenReturn(Optional.of(book));
        when(customerRepository.findById(recordDTO.getCustomerId())).thenReturn(Optional.of(customer));
        when(recordRepository.existsByBookAndCustomer(book, customer)).thenReturn(false);

        BorrowingRecord record = BorrowingRecord.builder()
                .customer(customer)
                .book(book)
                .borrowDate(LocalDate.parse(recordDTO.getBorrowDate()))
                .returnDate(LocalDate.parse(recordDTO.getReturnDate()))
                .build();

        when(recordRepository.save(any(BorrowingRecord.class))).thenReturn(record);

        ResponseEntity<BorrowingRecord> response = service.addRecord(recordDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(record, response.getBody());
        verify(bookRepository, times(1)).findById(recordDTO.getBookId());
        verify(customerRepository, times(1)).findById(recordDTO.getCustomerId());
        verify(recordRepository, times(1)).existsByBookAndCustomer(book, customer);
        verify(recordRepository, times(1)).save(any(BorrowingRecord.class));
    }

    @Test
    @DisplayName("TestUpdateRecord_RecordNotFound_ThrowDataNotFoundException")
    void testUpdateRecord_RecordNotFound_ThrowDataNotFoundException() {
        Long recordId = 1L;
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO();
        recordDTO.setBookId(1L);
        recordDTO.setCustomerId(1L);
        recordDTO.setBorrowDate("2023-06-01");
        recordDTO.setReturnDate("2023-06-10");

        when(recordRepository.findById(recordId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            service.updateRecord(recordId, recordDTO);
        });
        verify(recordRepository, times(1)).findById(recordId);
    }

    @Test
    @DisplayName("TestUpdateRecord_BookNotFound_ThrowDataNotFoundException")
    void testUpdateRecord_BookNotFound_ThrowDataNotFoundException() {
        Long recordId = 1L;
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO();
        recordDTO.setBookId(1L);
        recordDTO.setCustomerId(1L);
        recordDTO.setBorrowDate("2023-06-01");
        recordDTO.setReturnDate("2023-06-10");

        when(recordRepository.findById(recordId)).thenReturn(Optional.of(new BorrowingRecord()));
        when(bookRepository.findById(recordDTO.getBookId())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            service.updateRecord(recordId, recordDTO);
        });
        verify(recordRepository, times(1)).findById(recordId);
        verify(bookRepository, times(1)).findById(recordDTO.getBookId());
    }

    @Test
    @DisplayName("TestUpdateRecord_BookNotAvailable_ThrowBadRequestException")
    void testUpdateRecord_BookNotAvailable_ThrowBadRequestException() {
        Long recordId = 1L;
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO();
        recordDTO.setBookId(1L);
        recordDTO.setCustomerId(1L);
        recordDTO.setBorrowDate("2023-06-01");
        recordDTO.setReturnDate("2023-06-10");

        Book book = new Book();
        book.setId(1L);
        book.setAvailable(false);

        when(recordRepository.findById(recordId)).thenReturn(Optional.of(new BorrowingRecord()));
        when(bookRepository.findById(recordDTO.getBookId())).thenReturn(Optional.of(book));

        assertThrows(BadRequestException.class, () -> {
            service.updateRecord(recordId, recordDTO);
        });
        verify(recordRepository, times(1)).findById(recordId);
        verify(bookRepository, times(1)).findById(recordDTO.getBookId());
    }

    @Test
    @DisplayName("TestUpdateRecord_CustomerNotFound_ThrowDataNotFoundException")
    void testUpdateRecord_CustomerNotFound_ThrowDataNotFoundException() {
        Long recordId = 1L;
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO();
        recordDTO.setBookId(1L);
        recordDTO.setCustomerId(1L);
        recordDTO.setBorrowDate("2023-06-01");
        recordDTO.setReturnDate("2023-06-10");

        Book book = new Book();
        book.setId(1L);
        book.setAvailable(true);

        when(recordRepository.findById(recordId)).thenReturn(Optional.of(new BorrowingRecord()));
        when(bookRepository.findById(recordDTO.getBookId())).thenReturn(Optional.of(book));
        when(customerRepository.findById(recordDTO.getCustomerId())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            service.updateRecord(recordId, recordDTO);
        });
        verify(recordRepository, times(1)).findById(recordId);
        verify(bookRepository, times(1)).findById(recordDTO.getBookId());
        verify(customerRepository, times(1)).findById(recordDTO.getCustomerId());
    }

    @Test
    @DisplayName("TestUpdateRecord_InvalidDates_ThrowBadRequestException")
    void testUpdateRecord_InvalidDates_ThrowBadRequestException() {
        Long recordId = 1L;
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO();
        recordDTO.setBookId(1L);
        recordDTO.setCustomerId(1L);
        recordDTO.setBorrowDate("2023-06-10");
        recordDTO.setReturnDate("2023-06-01");

        Book book = new Book();
        book.setId(1L);
        book.setAvailable(true);

        Customer customer = new Customer();
        customer.setId(1L);

        when(recordRepository.findById(recordId)).thenReturn(Optional.of(new BorrowingRecord()));
        when(bookRepository.findById(recordDTO.getBookId())).thenReturn(Optional.of(book));
        when(customerRepository.findById(recordDTO.getCustomerId())).thenReturn(Optional.of(customer));

        assertThrows(BadRequestException.class, () -> {
            service.updateRecord(recordId, recordDTO);
        });
        verify(recordRepository, times(1)).findById(recordId);
        verify(bookRepository, times(1)).findById(recordDTO.getBookId());
        verify(customerRepository, times(1)).findById(recordDTO.getCustomerId());
    }

    @Test
    @DisplayName("TestUpdateRecord_ReturnRecord")
    void testUpdateRecord_ReturnRecord() {
        Long recordId = 1L;
        BorrowingRecordDTO recordDTO = new BorrowingRecordDTO();
        recordDTO.setBookId(1L);
        recordDTO.setCustomerId(1L);
        recordDTO.setBorrowDate("2023-06-01");
        recordDTO.setReturnDate("2023-06-10");

        Book book = new Book();
        book.setId(1L);
        book.setAvailable(true);

        Customer customer = new Customer();
        customer.setId(1L);

        BorrowingRecord existingRecord = new BorrowingRecord();
        existingRecord.setId(recordId);

        when(recordRepository.findById(recordId)).thenReturn(Optional.of(existingRecord));
        when(bookRepository.findById(recordDTO.getBookId())).thenReturn(Optional.of(book));
        when(customerRepository.findById(recordDTO.getCustomerId())).thenReturn(Optional.of(customer));

        BorrowingRecord updatedRecord = BorrowingRecord.builder()
                .id(recordId)
                .book(book)
                .customer(customer)
                .borrowDate(LocalDate.parse(recordDTO.getBorrowDate()))
                .returnDate(LocalDate.parse(recordDTO.getReturnDate()))
                .build();

        when(recordRepository.save(any(BorrowingRecord.class))).thenReturn(updatedRecord);

        ResponseEntity<BorrowingRecord> response = service.updateRecord(recordId, recordDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedRecord, response.getBody());
        verify(recordRepository, times(2)).findById(recordId);
        verify(bookRepository, times(1)).findById(recordDTO.getBookId());
        verify(customerRepository, times(1)).findById(recordDTO.getCustomerId());
        verify(recordRepository, times(1)).save(any(BorrowingRecord.class));
    }

    @Test
    @DisplayName("TestDeleteRecord_ReturnSuccessMessage")
    void testDeleteRecord_ReturnSuccessMessage() {
        Long recordId = 1L;
        BorrowingRecord record = new BorrowingRecord();
        record.setId(recordId);

        when(recordRepository.findById(recordId)).thenReturn(Optional.of(record));

        ResponseEntity<String> response = service.deleteRecord(recordId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(recordRepository, times(1)).findById(recordId);
        verify(recordRepository, times(1)).deleteById(recordId);
    }

    @Test
    @DisplayName("TestDeleteRecord_ThrowDataNotFoundException")
    void testDeleteRecord_ThrowDataNotFoundException() {
        Long recordId = 1L;

        when(recordRepository.findById(recordId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            service.deleteRecord(recordId);
        });
        verify(recordRepository, times(1)).findById(recordId);
        verify(recordRepository, never()).deleteById(recordId);
    }
}