package com.example.libraryManagementSystem.exceptionhandling;

public class DataAlreadyExistException extends RuntimeException {
    public DataAlreadyExistException(String message) {
        super(message);
    }
}
