package com.example.digitallogistics.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleValidation_ShouldReturnBadRequest() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "error message");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals("validation_failed", body.get("error"));
        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("errors"));
    }

    @Test
    void handleResourceNotFound_ShouldReturnNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals("resource_not_found", body.get("error"));
        assertEquals("Resource not found", body.get("message"));
        assertTrue(body.containsKey("timestamp"));
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest() {
        ValidationException ex = new ValidationException("Validation error");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals("validation_error", body.get("error"));
        assertEquals("Validation error", body.get("message"));
        assertTrue(body.containsKey("timestamp"));
    }

    @Test
    void handleDataIntegrityViolation_UniqueConstraint_ShouldReturnConflict() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("unique constraint violation");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleDataIntegrityViolation(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals("data_integrity_violation", body.get("error"));
        assertEquals("Duplicate entry: A record with this value already exists", body.get("message"));
    }

    @Test
    void handleDataIntegrityViolation_ForeignKey_ShouldReturnConflict() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("foreign key constraint");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleDataIntegrityViolation(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals("Cannot complete operation: Referenced record does not exist or is still in use", body.get("message"));
    }

    @Test
    void handleAccessDenied_ShouldReturnForbidden() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleAccessDenied(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals("access_denied", body.get("error"));
        assertEquals("You don't have permission to access this resource", body.get("message"));
    }

    @Test
    void handleAuthentication_ShouldReturnUnauthorized() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleAuthentication(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals("authentication_failed", body.get("error"));
        assertEquals("Invalid credentials or authentication required", body.get("message"));
    }

    @Test
    void handleExpiredJwt_ShouldReturnUnauthorized() {
        ExpiredJwtException ex = mock(ExpiredJwtException.class);

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleExpiredJwt(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals("token_expired", body.get("error"));
        assertEquals("Your session has expired. Please login again", body.get("message"));
    }

    @Test
    void handleInvalidJwt_MalformedJwt_ShouldReturnUnauthorized() {
        MalformedJwtException ex = new MalformedJwtException("Malformed JWT");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleInvalidJwt(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals("invalid_token", body.get("error"));
        assertEquals("Invalid authentication token", body.get("message"));
    }

    @Test
    void handleInvalidJwt_SignatureException_ShouldReturnUnauthorized() {
        SignatureException ex = new SignatureException("Invalid signature");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleInvalidJwt(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals("invalid_token", body.get("error"));
    }

    @Test
    void handleOther_ShouldReturnInternalServerError() {
        RuntimeException ex = new RuntimeException("Unexpected error");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleOther(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals("internal_error", body.get("error"));
        assertEquals("An unexpected error occurred. Please try again later", body.get("message"));
    }
}