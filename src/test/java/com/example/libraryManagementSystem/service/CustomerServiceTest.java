package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.dto.CustomerDTO;
import com.example.libraryManagementSystem.exceptionhandling.DataAlreadyExistException;
import com.example.libraryManagementSystem.exceptionhandling.DataNotFoundException;
import com.example.libraryManagementSystem.model.Customer;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private CustomerService service;

    @BeforeEach
    void setUp() {
        service = new CustomerService(repository, passwordEncoder);
    }

    @AfterEach
    void tearDown() {
        service = null;
    }

    @Test
    @DisplayName("TestGetCustomers_ReturnCustomerList")
    void testGetCustomers_ReturnCustomerList() {
        List<Customer> customers = Arrays.asList(
                new Customer(1L, "Customer", "customer@example.com", "010101010101", "123 Street", "Abc123456"),
                new Customer(2L, "Customer2", "custome2r@example.com", "010101010221", "123 Street", "Abc123456")
        );
        Page<Customer> customersPage = new PageImpl<>(customers);
        when(repository.findAll()).thenReturn(customers);
        when(repository.findAll(any(Pageable.class))).thenReturn(customersPage);

        ResponseEntity<List<Customer>> response = service.getCustomers(0, 5, "id");

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertIterableEquals(customers, response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    @DisplayName("TestGetCustomers_ReturnCustomersList_InvalidPaginationValues")
    void testGetCustomers_ReturnCustomersList_InvalidPaginationValues() {
        List<Customer> customers = Arrays.asList(
                new Customer(1L, "Customer", "customer@example.com", "010101010101", "123 Street", "Abc123456"),
                new Customer(2L, "Customer2", "custome2r@example.com", "010101010221", "123 Street", "Abc123456")
        );

        int pageNumber = -1;
        int pageSize = 0;
        String field = "Wrong";

        Page<Customer> customersPage = new PageImpl<>(customers);
        when(repository.findAll()).thenReturn(customers);
        when(repository.findAll(any(Pageable.class))).thenReturn(customersPage);

        ResponseEntity<List<Customer>> response = service.getCustomers(pageNumber, pageSize, field);

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertIterableEquals(customers, response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    @DisplayName("TestGetCustomers_ThrowDataNotFoundException")
    void testGetCustomers_ThrowDataNotFoundException() {

        when(repository.findAll()).thenReturn(List.of());

        assertThrows(DataNotFoundException.class, () -> {
            service.getCustomers(0, 5, "id");
        });
    }

    @Test
    @DisplayName("TestGetCustomerById_ReturnCustomer")
    void testGetCustomerById_ReturnCustomer() {
        Customer customer = new Customer(1L, "Customer", "customer@example.com", "01010101011", "123 Street", "Abc123456");
        when(repository.findById(customer.getId())).thenReturn(Optional.of(customer));

        ResponseEntity<Customer> response = service.getCustomerById(1L);

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customer, response.getBody());
    }

    @Test
    @DisplayName("TestGetCustomerById_ThrowDataNotFoundException")
    void testGetCustomerById_ThrowDataNotFoundException() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            service.getCustomerById(1L);
        });
    }

    @Test
    @DisplayName("TestAddCustomer_ReturnSavedCustomer")
    void testAddCustomer_ReturnSavedCustomer() {
        CustomerDTO customerDTO = new CustomerDTO("Customer", "customer@example.com", "01234567890", "123 Street", "Pa$s1234");

        Customer newCustomer = Customer
                .builder()
                .name(customerDTO.getName())
                .address(customerDTO.getAddress())
                .email(customerDTO.getEmail())
                .phoneNumber(customerDTO.getPhoneNumber())
                .password(passwordEncoder.encode(customerDTO.getPassword()))
                .build();

        when(repository.existsByEmailOrPhoneNumber(customerDTO.getEmail(), customerDTO.getPhoneNumber())).thenReturn(false);

        when(repository.save(newCustomer)).thenReturn(newCustomer);

        ResponseEntity<Customer> response = service.addCustomer(customerDTO);

        // Check the response entity body and status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newCustomer, response.getBody());
    }

    @Test
    @DisplayName("TestAddCustomer_ThrowDataAlreadyExistException")
    void testAddCustomer_ThrowsDataAlreadyExistException() {
        CustomerDTO customerDTO = new CustomerDTO("Customer", "customer@example.com", "01234567890", "123 Street", "Pa$s1234");

        when(repository.existsByEmailOrPhoneNumber(customerDTO.getEmail(), customerDTO.getPhoneNumber())).thenReturn(true);

        assertThrows(DataAlreadyExistException.class, () -> {
            service.addCustomer(customerDTO);
        });
    }

    @Test
    @DisplayName("TestUpdateCustomer_ReturnUpdatedCustomer")
    void testUpdateCustomer_ReturnUpdatedCustomer() {
        Long customerId = 1L;
        CustomerDTO customerDTO = new CustomerDTO("Updated", "customerNew@example.com", "01134567890", "1243 Street", "Pa$ss1234");

        Customer updatedCustomer = Customer
                .builder()
                .id(customerId)
                .name(customerDTO.getName())
                .address(customerDTO.getAddress())
                .email(customerDTO.getEmail())
                .phoneNumber(customerDTO.getPhoneNumber())
                .password(passwordEncoder.encode(customerDTO.getPassword()))
                .build();

        Customer existingCustomer = new Customer(1L, "Existing", "customerOld@example.com", "01234567890", "123 Street", passwordEncoder.encode("Pa$s1234"));

        when(repository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(repository.save(updatedCustomer)).thenReturn(updatedCustomer);

        ResponseEntity<Customer> response = service.updateCustomer(customerId, customerDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existingCustomer, updatedCustomer);
        assertEquals(updatedCustomer, response.getBody());
    }

    @Test
    @DisplayName("TestUpdateCustomer_ThrowDataNotFoundException")
    void testUpdateCustomer_ThrowDataNotFoundException() {
        Long customerId = 999L;
        CustomerDTO customerDTO = new CustomerDTO("Customer", "customer@example.com", "01234567890", "123 Street", "Pa$s1234");

        when(repository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            service.updateCustomer(customerId, customerDTO);
        });
    }

    @Test
    @DisplayName("TestDeleteCustomer_ReturnSuccessMessage")
    void testDeleteCustomer_ReturnSuccessMessage() {
        Long customerId = 1L;
        Customer customer = new Customer(1L, "Customer", "customer@example.com", "01234567890", "123 Street", passwordEncoder.encode("Pa$s1234"));

        when(repository.findById(customerId)).thenReturn(Optional.of(customer));

        ResponseEntity<String> response = service.deleteCustomer(customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(repository, times(1)).findById(customerId);
        verify(repository, times(1)).deleteById(customerId);
    }

    @Test
    @DisplayName("TestDeleteCustomer_ThrowDataNotFoundException")
    void testDeleteCustomer_ThrowDataNotFoundException() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Long id = 1L;

        assertThrows(DataNotFoundException.class, () -> {
            service.deleteCustomer(id);
        });
    }
}