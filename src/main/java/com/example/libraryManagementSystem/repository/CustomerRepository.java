package com.example.libraryManagementSystem.repository;

import com.example.libraryManagementSystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmailOrPhoneNumber(String email, String phoneNumber);

}
