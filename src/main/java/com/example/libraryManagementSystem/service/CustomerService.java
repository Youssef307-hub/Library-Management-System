package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.dto.CustomerDTO;
import com.example.libraryManagementSystem.exceptionhandling.DataAlreadyExistException;
import com.example.libraryManagementSystem.exceptionhandling.DataNotFoundException;
import com.example.libraryManagementSystem.model.Customer;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Cacheable("customers")
    public ResponseEntity<List<Customer>> getCustomers(int pageNumber, int pageSize, String field) {

        if (repository.findAll().isEmpty())
            throw new DataNotFoundException("No Customers Found!");

        if (pageNumber <= 0)
            pageNumber = 0;

        if (pageSize <= 0)
            pageSize = 5;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(field));

        Page<Customer> customersPage = repository.findAll(pageable);

        return new ResponseEntity<>(customersPage.getContent(), HttpStatus.OK);
    }

    @Cacheable("customers")
    public ResponseEntity<Customer> getCustomerById(Long id) {
        if (repository.findById(id).isEmpty())
            throw new DataNotFoundException("No Customer With The ID: " + id + " Found!");

        return new ResponseEntity<>(repository.findById(id).get(), HttpStatus.OK);
    }

    @CacheEvict(value = "customers", allEntries = true)
    public ResponseEntity<Customer> addCustomer(CustomerDTO customerDTO) {
        if (repository.existsByEmailOrPhoneNumber(customerDTO.getEmail(), customerDTO.getPhoneNumber()))
            throw new DataAlreadyExistException("This Customer Already Exists!");

        String encodedPassword = passwordEncoder.encode(customerDTO.getPassword());

        Customer newCustomer = Customer
                .builder()
                .name(customerDTO.getName())
                .address(customerDTO.getAddress())
                .email(customerDTO.getEmail())
                .phoneNumber(customerDTO.getPhoneNumber())
                .password(encodedPassword)
                .build();

        return new ResponseEntity<>(repository.save(newCustomer), HttpStatus.OK);
    }

    @CacheEvict(value = "customers", allEntries = true)
    public ResponseEntity<Customer> updateCustomer(Long id, CustomerDTO customerDTO) {
        if (repository.findById(id).isEmpty())
            throw new DataNotFoundException("No Customer With The ID: " + id + " Found!");

        Customer updatedCustomer = repository.findById(id).get();
        updatedCustomer.setName(customerDTO.getName());
        updatedCustomer.setAddress(customerDTO.getAddress());
        updatedCustomer.setEmail(customerDTO.getEmail());
        updatedCustomer.setPhoneNumber(customerDTO.getPhoneNumber());
        String encodedPassword = passwordEncoder.encode(customerDTO.getPassword());
        updatedCustomer.setPassword(encodedPassword);

        return new ResponseEntity<>(repository.save(updatedCustomer), HttpStatus.OK);
    }

    @CacheEvict(value = "customers", allEntries = true)
    public ResponseEntity<String> deleteCustomer(Long id) {
        if (repository.findById(id).isEmpty())
            throw new DataNotFoundException("No Customer With The ID: " + id + " Found!");

        repository.deleteById(id);

        return new ResponseEntity<>("Customer With ID: " + id + " Deleted Successfully!", HttpStatus.OK);
    }

}
