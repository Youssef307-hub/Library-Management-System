package com.example.libraryManagementSystem.controller;

import com.example.libraryManagementSystem.dto.CustomerDTO;
import com.example.libraryManagementSystem.exceptionhandling.DataAlreadyExistException;
import com.example.libraryManagementSystem.exceptionhandling.DataNotFoundException;
import com.example.libraryManagementSystem.model.Customer;
import com.example.libraryManagementSystem.service.CustomerService;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = CustomerRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class CustomerRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    private String asJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("TestGetCustomers_ReturnCustomersList")
    void testGetCustomers_ReturnCustomersList() throws Exception {

        List<Customer> customers = Arrays.asList(
                new Customer(1L, "Customer 1", "customer1@example.com", "0124567890", "123 Main St", "Pa$s1234"),
                new Customer(2L, "Customer 2", "customer2@example.com", "0117654321", "456 Elm St", "Pa$s1234")
        );

        when(customerService.getCustomers(anyInt(), anyInt(), anyString()))
                .thenReturn(ResponseEntity.ok(customers));

        mockMvc.perform(get("/api/v1/library/customers")
                        .param("pageNumber", "0")
                        .param("pageSize", "5")
                        .param("field", "id"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Customer 1"))
                .andExpect(jsonPath("$[0].email").value("customer1@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Customer 2"))
                .andExpect(jsonPath("$[1].email").value("customer2@example.com"));

        verify(customerService).getCustomers(0, 5, "id");
    }

    @Test
    @DisplayName("TestGetCustomers_ThrowDataNotFoundException")
    void testGetCustomers_ThrowDataNotFoundException() throws Exception {

        when(customerService.getCustomers(anyInt(), anyInt(), anyString()))
                .thenThrow(new DataNotFoundException("No Customers Found!"));


        mockMvc.perform(get("/api/v1/library/customers"))
                .andExpect(status().isNotFound());

        verify(customerService).getCustomers(0, 5, "id");
    }

    @Test
    @DisplayName("TestGetCustomerById_ReturnCustomer")
    void testGetCustomerById_ReturnCustomer() throws Exception {
        Customer customer = new Customer(1L, "Customer 1", "customer1@example.com", "01245678900", "123 Main St", "Pa$s1234");

        when(customerService.getCustomerById(anyLong())).thenReturn(ResponseEntity.ok(customer));

        mockMvc.perform(get("/api/v1/library/customers/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Customer 1"))
                .andExpect(jsonPath("$.email").value("customer1@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("01245678900"))
                .andExpect(jsonPath("$.address").value("123 Main St"));


        verify(customerService).getCustomerById(1L);
    }

    @Test
    @DisplayName("TestGetCustomerById_ThrowDataNotFoundException")
    void testGetCustomerById_ThrowDataNotFoundException() throws Exception {
        when(customerService.getCustomerById(anyLong()))
                .thenThrow(new DataNotFoundException("No Customer With The ID: " + anyLong() + " Found!"));


        mockMvc.perform(get("/api/v1/library/customers/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(customerService).getCustomerById(1L);
    }

    @Test
    @DisplayName("TestAddCustomer_ReturnSavedCustomer")
    void testAddCustomer_ReturnSavedCustomer() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO("Customer", "Customer@example.com", "01245678900", "123 Main St", "Pa$s1234");
        Customer savedCustomer = new Customer(1L, "Customer", "Customer@example.com", "01245678900", "123 Main St", "Pa$s1234");

        when(customerService.addCustomer(any(CustomerDTO.class)))
                .thenReturn(ResponseEntity.ok(savedCustomer));

        mockMvc.perform(post("/api/v1/library/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Customer"))
                .andExpect(jsonPath("$.email").value("Customer@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("01245678900"))
                .andExpect(jsonPath("$.address").value("123 Main St"));

        verify(customerService).addCustomer(any(CustomerDTO.class));
    }

    @Test
    @DisplayName("TestAddCustomer_ThrowBadRequestException")
    void testAddCustomer_ThrowBadRequestException() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO("", "invalid-email", "123", "123 Main St", "weakpassword");

        mockMvc.perform(post("/api/v1/library/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDTO)))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).addCustomer(any(CustomerDTO.class));
    }

    @Test
    @DisplayName("TestAddCustomer_ThrowDataAlreadyExistException")
    void testAddCustomer_ThrowDataAlreadyExistException() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO("Customer", "Customer@example.com", "01245678900", "123 Main St", "Pa$s1234");

        when(customerService.addCustomer(any(CustomerDTO.class)))
                .thenThrow(new DataAlreadyExistException("This Customer Already Exists!"));

        mockMvc.perform(post("/api/v1/library/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDTO)))
                .andExpect(status().isConflict());

        verify(customerService).addCustomer(any(CustomerDTO.class));
    }

    @Test
    @DisplayName("TestUpdateCustomer_ReturnUpdatedCustomer")
    void testUpdateCustomer_ReturnUpdatedCustomer() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO("NewCustomer", "newCustomer@example.com", "01245678000", "123 Main St", "Pa$s1234");
        Customer updatedCustomer = new Customer(1L, "Customer", "Customer@example.com", "01245678900", "123 Main St", "Pa$s1234");

        when(customerService.updateCustomer(anyLong(), any(CustomerDTO.class)))
                .thenReturn(ResponseEntity.ok(updatedCustomer));

        mockMvc.perform(put("/api/v1/library/customers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDTO)))
                .andExpect(status().isOk());

        verify(customerService).updateCustomer(anyLong(), any(CustomerDTO.class));
    }

    @Test
    @DisplayName("TestUpdateCustomer_ThrowBadRequestException")
    void testUpdateCustomer_ThrowBadRequestException() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO("", "invalid-email", "123", "123 Main St", "weakpassword");

        mockMvc.perform(put("/api/v1/library/customers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDTO)))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).updateCustomer(anyLong(), any(CustomerDTO.class));
    }

    @Test
    @DisplayName("TestUpdateCustomer_ThrowDataNotFoundException")
    void testUpdateCustomer_ThrowDataNotFoundException() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO("Customer", "Customer@example.com", "01245678900", "123 Main St", "Pa$s1234");

        when(customerService.updateCustomer(anyLong(), any(CustomerDTO.class)))
                .thenThrow(new DataNotFoundException("No Customer With The ID: " + 1L + " Found!"));

        mockMvc.perform(put("/api/v1/library/customers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No Customer With The ID: 1 Found!"));

        verify(customerService).updateCustomer(anyLong(), any(CustomerDTO.class));
    }

    @Test
    @DisplayName("TestDeleteCustomer_Success")
    void testDeleteCustomer_ReturnSuccessMessage() throws Exception {
        when(customerService.deleteCustomer(anyLong()))
                .thenReturn(ResponseEntity.ok("Customer deleted successfully"));

        mockMvc.perform(delete("/api/v1/library/customers/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer deleted successfully"));

        verify(customerService).deleteCustomer(anyLong());
    }

    @Test
    @DisplayName("TestDeleteCustomer_ThrowDataNotFoundException")
    void testDeleteCustomer_ThrowDataNotFoundException() throws Exception {
        when(customerService.deleteCustomer(anyLong()))
                .thenThrow(new DataNotFoundException("No Customer With The ID: " + 1L + " Found!"));

        mockMvc.perform(delete("/api/v1/library/customers/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No Customer With The ID: 1 Found!"));

        verify(customerService).deleteCustomer(anyLong());
    }

}