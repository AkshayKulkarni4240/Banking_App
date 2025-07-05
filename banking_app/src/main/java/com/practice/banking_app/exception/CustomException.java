package com.practice.banking_app.exception;

public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}