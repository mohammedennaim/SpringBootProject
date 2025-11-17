package com.example.digitallogistics.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionClassesTest {

    @Test
    void invalidTokenException_WithMessage_ShouldCreateException() {
        String message = "Invalid token";
        InvalidTokenException exception = new InvalidTokenException(message);
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void invalidTokenException_WithMessageAndCause_ShouldCreateException() {
        String message = "Invalid token";
        Throwable cause = new RuntimeException("Root cause");
        InvalidTokenException exception = new InvalidTokenException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void orderStateException_WithMessage_ShouldCreateException() {
        String message = "Invalid order state";
        OrderStateException exception = new OrderStateException(message);
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void orderStateException_WithMessageAndCause_ShouldCreateException() {
        String message = "Invalid order state";
        Throwable cause = new RuntimeException("Root cause");
        OrderStateException exception = new OrderStateException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void resourceNotFoundException_WithMessage_ShouldCreateException() {
        String message = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void validationException_WithMessage_ShouldCreateException() {
        String message = "Validation failed";
        ValidationException exception = new ValidationException(message);
        
        assertEquals(message, exception.getMessage());
    }
}