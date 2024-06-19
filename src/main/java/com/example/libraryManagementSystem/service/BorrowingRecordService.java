package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.dto.BorrowingRecordDTO;
import com.example.libraryManagementSystem.exceptionhandling.BadRequestException;
import com.example.libraryManagementSystem.exceptionhandling.DataAlreadyExistException;
import com.example.libraryManagementSystem.exceptionhandling.DataNotFoundException;
import com.example.libraryManagementSystem.model.Book;
import com.example.libraryManagementSystem.model.BorrowingRecord;
import com.example.libraryManagementSystem.model.Customer;
import com.example.libraryManagementSystem.repository.BookRepository;
import com.example.libraryManagementSystem.repository.BorrowingRecordRepository;
import com.example.libraryManagementSystem.repository.CustomerRepository;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BorrowingRecordService {

    private final BorrowingRecordRepository recordRepository;
    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;

    @Cacheable("records")
    public ResponseEntity<List<BorrowingRecord>> getRecords(int pageNumber, int pageSize, String field) {

        if (recordRepository.findAll().isEmpty())
            throw new DataNotFoundException("No Records Found!");

        if (pageNumber <= 0)
            pageNumber = 0;

        if (pageSize <= 0)
            pageSize = 5;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(field));

        Page<BorrowingRecord> recordsPage = recordRepository.findAll(pageable);

        return new ResponseEntity<>(recordsPage.getContent(), HttpStatus.OK);
    }

    @Cacheable("records")
    public ResponseEntity<List<BorrowingRecord>> searchRecords(Long customerId, Long bookId) {
        int nonNullParamsCount = 0;
        if (customerId != null)
            nonNullParamsCount++;
        if (bookId != null)
            nonNullParamsCount++;

        if (nonNullParamsCount == 0) {
            throw new BadRequestException("At least one search parameter must be provided.");
        } else if (nonNullParamsCount > 1) {
            throw new BadRequestException("Only one search parameter can be provided at a time.");
        }

        if (bookId != null)
            return getRecordsByBook(bookId);
        else
            return getRecordsByCustomer(customerId);
    }

    private ResponseEntity<List<BorrowingRecord>> getRecordsByCustomer(Long customerId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerRepository.findAll().isEmpty() || customerOptional.isEmpty() || recordRepository.findByCustomer(customerOptional.get()).isEmpty())
            throw new DataNotFoundException("No Record Found!");

        return new ResponseEntity<>(recordRepository.findByCustomer(customerOptional.get()), HttpStatus.OK);
    }

    private ResponseEntity<List<BorrowingRecord>> getRecordsByBook(Long bookId) {

        Optional<Book> bookOptional = bookRepository.findById(bookId);

        if (bookRepository.findAll().isEmpty() || bookOptional.isEmpty() || recordRepository.findByBook(bookOptional.get()).isEmpty())
            throw new DataNotFoundException("No Record Found!");

        return new ResponseEntity<>(recordRepository.findByBook(bookOptional.get()), HttpStatus.OK);
    }

    @Cacheable("records")
    public ResponseEntity<BorrowingRecord> getRecordById(Long id) {
        if (recordRepository.findById(id).isEmpty())
            throw new DataNotFoundException("No Record With The ID: " + id + " Found!");

        return new ResponseEntity<>(recordRepository.findById(id).get(), HttpStatus.OK);
    }

    @CacheEvict(value = "records", allEntries = true)
    public ResponseEntity<BorrowingRecord> addRecord(BorrowingRecordDTO recordDTO) {

        Book book = bookRepository.findById(recordDTO.getBookId())
                .orElseThrow(() -> new DataNotFoundException("No Book With That ID Found!"));

        if (!book.isAvailable())
            throw new BadRequestException("This Book Is Not Available!");

        Customer customer = customerRepository.findById(recordDTO.getCustomerId())
                .orElseThrow(() -> new DataNotFoundException("No Customer With That ID Found!"));

        if (recordRepository.existsByBookAndCustomer(book, customer))
            throw new DataAlreadyExistException("This Record Already Exists!");

        LocalDate borrowDate = LocalDate.parse(recordDTO.getBorrowDate());
        LocalDate returnDate = LocalDate.parse(recordDTO.getReturnDate());

        if (borrowDate.isAfter(returnDate))
            throw new BadRequestException("Borrow Date can't be before Return Date!");

        BorrowingRecord record = BorrowingRecord
                .builder()
                .customer(customer)
                .book(book)
                .borrowDate(borrowDate)
                .returnDate(returnDate)
                .build();

        return new ResponseEntity<>(recordRepository.save(record), HttpStatus.OK);
    }

    @CacheEvict(value = "records", allEntries = true)
    public ResponseEntity<BorrowingRecord> updateRecord(Long id, BorrowingRecordDTO recordDTO) {
        if (recordRepository.findById(id).isEmpty())
            throw new DataNotFoundException("No Record With The ID: " + id + " Found!");

        Book book = bookRepository.findById(recordDTO.getBookId())
                .orElseThrow(() -> new DataNotFoundException("No Book With That ID Found!"));

        if (!book.isAvailable())
            throw new BadRequestException("This Book Is Not Available!");

        Customer customer = customerRepository.findById(recordDTO.getCustomerId())
                .orElseThrow(() -> new DataNotFoundException("No Customer With That ID Found!"));

        LocalDate borrowDate = LocalDate.parse(recordDTO.getBorrowDate());
        LocalDate returnDate = LocalDate.parse(recordDTO.getReturnDate());

        if (borrowDate.isAfter(returnDate))
            throw new BadRequestException("Borrow Date can't be before Return Date!");

        BorrowingRecord updatedRecord = recordRepository.findById(id).get();
        updatedRecord.setBook(book);
        updatedRecord.setCustomer(customer);
        updatedRecord.setBorrowDate(borrowDate);
        updatedRecord.setReturnDate(returnDate);

        return new ResponseEntity<>(recordRepository.save(updatedRecord), HttpStatus.OK);
    }

    @CacheEvict(value = "records", allEntries = true)
    public ResponseEntity<String> deleteRecord(Long id) {
        if (recordRepository.findById(id).isEmpty())
            throw new DataNotFoundException("No Record With The ID: " + id + " Found!");

        recordRepository.deleteById(id);

        return new ResponseEntity<>("Record With ID: " + id + " Deleted Successfully!", HttpStatus.OK);
    }
}
