package com.example.libraryManagementSystem.exceptionhandling;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
