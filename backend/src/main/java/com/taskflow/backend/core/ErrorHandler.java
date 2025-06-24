package com.taskflow.backend.core;

import com.taskflow.backend.core.exceptions.*;
import com.taskflow.backend.dto.ResponseMessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for custom application exceptions.
 */
@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle validation errors (e.g. from @Valid annotated DTOs)
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle already existing object (e.g username already in use)
     */
    @ExceptionHandler(AppObjectAlreadyExistsException.class)
    public ResponseEntity<ResponseMessageDTO> handleAlreadyExists(AppObjectAlreadyExistsException ex) {

        return new ResponseEntity<>(new ResponseMessageDTO(ex.getCode(), ex.getMessage()), HttpStatus.CONFLICT);
    }

    /**
     * Handle bad arguments (e.g wrong format or business rule violation)
     */
    @ExceptionHandler(AppObjectInvalidArgumentException.class)
    public ResponseEntity<ResponseMessageDTO> handleInvalidArgument(AppObjectInvalidArgumentException ex) {

        return new ResponseEntity<>(new ResponseMessageDTO(ex.getCode(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle entity not found (e.g user or task not found)
     */
    @ExceptionHandler(AppObjectNotFoundException.class)
    public ResponseEntity<ResponseMessageDTO> handleNotFound(AppObjectNotFoundException ex) {

        return new ResponseEntity<>(new ResponseMessageDTO(ex.getCode(), ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Handle not authorized actions (e.g. user without permission)
     */
    @ExceptionHandler(AppObjectNotAuthorizedException.class)
    public ResponseEntity<ResponseMessageDTO> handleNotAuthorized(AppObjectNotAuthorizedException ex) {

        return new ResponseEntity<>(new ResponseMessageDTO(ex.getCode(), ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    /**
     * Handle generic server errors (internal logic failures)
     */
    @ExceptionHandler(AppServerException.class)
    public ResponseEntity<ResponseMessageDTO> handleServerError(AppServerException ex) {

        return new ResponseEntity<>(new ResponseMessageDTO(ex.getCode(), ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle any other Exception that has not caught
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessageDTO> handleGenericException(Exception ex) {

        return new ResponseEntity<>(new ResponseMessageDTO("Internal Error",
                "Something went wrong. Please try again"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
