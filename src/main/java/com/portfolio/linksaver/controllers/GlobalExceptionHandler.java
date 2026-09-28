package com.portfolio.linksaver.controllers;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.portfolio.linksaver.dto.ErrorResponse;
import com.portfolio.linksaver.security.SafeUrlValidator.BlockedUrlException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BlockedUrlException.class)
    public ResponseEntity<ErrorResponse> handleBlockedUrlException(BlockedUrlException ex) {
        // Powód odrzucenia trafia tylko do logów - dla klienta jeden ogólny komunikat,
        // żeby nie zdradzać, co dokładnie wykryła walidacja.
        log.warn("Zablokowano adres URL: {}", ex.getMessage());

        ErrorResponse errorInfo = new ErrorResponse("Invalid URL", HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorInfo);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorInfo = new ErrorResponse(ex.getMessage(), HttpStatus.CONFLICT.value(), LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorInfo);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRunTimeException(RuntimeException ex) {
        ErrorResponse errorInfo = new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorInfo);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String cleanMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        ErrorResponse errorInfo = new ErrorResponse(cleanMessage, HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());

        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorInfo);
    }
}
