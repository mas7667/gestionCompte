package com.gestioncompte.gestion_compte.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handlesInsufficientBalance() {
        IllegalStateException ex = new IllegalStateException("Insufficient balance. Current balance: 10, requested: 50");

        ResponseEntity<Object> response = handler.handleIllegalState(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertBody(response, "Insufficient balance. Current balance: 10, requested: 50");
    }

    @Test
    void handlesEmailAlreadyUsed() {
        IllegalArgumentException ex = new IllegalArgumentException("An account already exists with this email.");

        ResponseEntity<Object> response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertBody(response, "An account already exists with this email.");
    }

    @Test
    void handlesAccountNotFound() {
        NoSuchElementException ex = new NoSuchElementException("No account found with number: XYZ");

        ResponseEntity<Object> response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertBody(response, "No account found with number: XYZ");
    }

    @Test
    void handlesValidationFailure() {
        FieldError fieldError = new FieldError("transactionRequest", "amount", "must be greater than 0.01");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        MethodParameter parameter = mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Object> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertBody(response, "amount: must be greater than 0.01");
    }

    @SuppressWarnings("unchecked")
    private void assertBody(ResponseEntity<Object> response, String expectedMessage) {
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(expectedMessage, body.get("message"));
    }
}