package com.sebastian.dev.projecttaskmanagement.exception.advice;

import com.sebastian.dev.projecttaskmanagement.controller.dto.ErrorResponseDTO;
import com.sebastian.dev.projecttaskmanagement.exception.BusinessRuleViolationException;
import com.sebastian.dev.projecttaskmanagement.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        p.setTitle("Bad Request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(p);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ProblemDetail> handleBusinessRuleViolationException(BusinessRuleViolationException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        p.setTitle("Business Rule Problem");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(p);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        p.setTitle("Resource Not Found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(p);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT,
                "validation failed for one or more fields");
        p.setTitle("Invalid Request Argument");
        List<ErrorResponseDTO> errors = ex.getFieldErrors()
                .stream()
                .map(fe -> new ErrorResponseDTO(fe.getField(), fe.getDefaultMessage(), fe.getCode()))
                .toList();

        p.setProperty("errors", errors);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(p);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(AccessDeniedException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        p.setTitle("Access Denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(p);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        ProblemDetail pm = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "There was an unexpected error, try again later");
        pm.setTitle("Unexpected error");
        // p.setInstance(URI.create(request.getDescription(false))); Spring
        // automatically sets it
        // p.setProperty("stack_trace", ex.getStackTrace()); logs for us.
        pm.setProperty("Error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pm);
    }
}
