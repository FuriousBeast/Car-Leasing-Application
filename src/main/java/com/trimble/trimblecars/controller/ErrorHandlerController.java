package com.trimble.trimblecars.controller;

import com.trimble.trimblecars.dto.ApiResponse;
import com.trimble.trimblecars.dto.ResponseType;
import com.trimble.trimblecars.exception.ActionNotAllowedException;
import com.trimble.trimblecars.exception.FieldMissingException;
import com.trimble.trimblecars.exception.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * ErrorHandlerController.java
 *
 * @author Nandhakumar N 
 * @module com.trimble.trimblecars.controller
 * @created Nov 23, 2024
 */

@ControllerAdvice
@RestController
public class ErrorHandlerController
{
    private static final Logger logger = LogManager.getLogger();

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<ApiResponse<String>> handleUserNotFound(NotFoundException exception)
    {
        logger.error(exception.getMessage());
        ApiResponse<String> apiResponse = ApiResponse.<String>builder().responseType(ResponseType.ERROR).responseMessage(exception.getMessage()).build();
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ActionNotAllowedException.class})
    public ResponseEntity<ApiResponse<String>> handleActionNotAllowed(ActionNotAllowedException exception)
    {
        logger.error(exception.getMessage());
        ApiResponse<String> apiResponse = ApiResponse.<String>builder().responseType(ResponseType.ERROR).responseMessage(exception.getMessage()).build();
        return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {FieldMissingException.class})
    public ResponseEntity<ApiResponse<String>> handleFieldMissing(FieldMissingException exception)
    {
        logger.error(exception.getMessage());
        ApiResponse<String> apiResponse = ApiResponse.<String>builder().responseType(ResponseType.ERROR).responseMessage(exception.getMessage()).build();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<ApiResponse<String>> handleAccessDenied(AccessDeniedException exception)
    {
        logger.error(exception.getMessage());
        ApiResponse<String> apiResponse = ApiResponse.<String>builder().responseType(ResponseType.ERROR).responseMessage(exception.getMessage()).build();
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<ApiResponse<String>> handleRunTime(RuntimeException exception)
    {
        logger.error("{} {}",exception.getClass() ,exception.getMessage());
        ApiResponse<String> apiResponse = ApiResponse.<String>builder().responseType(ResponseType.ERROR).responseMessage(exception.getMessage()).build();
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
