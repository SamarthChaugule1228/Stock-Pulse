package com.stockpulse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> notFound(ResourceNotFoundException exception) {
		return response(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler({InvalidSuggestionException.class, IllegalArgumentException.class, DataIntegrityViolationException.class})
	public ResponseEntity<?> invalid(RuntimeException exception) {
		return response(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> validation(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).findFirst().orElse("Invalid request");
		return response(HttpStatus.BAD_REQUEST, message);
	}

	private ResponseEntity<?> response(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(Map.of("timestamp", Instant.now(), "status", status.value(), "error", message));
	}
}
