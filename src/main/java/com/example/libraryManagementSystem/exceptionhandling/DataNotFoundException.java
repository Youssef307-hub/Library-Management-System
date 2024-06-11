package com.example.libraryManagementSystem.exceptionhandling;

public class DataNotFoundException extends RuntimeException{
    public DataNotFoundException(String message) {
        super(message);
    }
}
