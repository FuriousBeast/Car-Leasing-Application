package com.nk.cars.controller;

import com.nk.cars.dto.ApiResponse;
import com.nk.cars.dto.ResponseType;
import com.nk.cars.exception.ActionNotAllowedException;
import com.nk.cars.exception.FieldMissingException;
import com.nk.cars.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

/**
 * ErrorHandlerController.java
 *
 * @author Nandhakumar N 
 * @module com.nk.cars.controller
 * @created Nov 23, 2024
 */

@ControllerAdvice
@RestController
public class ErrorHandlerController
{
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private HttpServletRequest httpServletRequest;

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<ApiResponse<String>> handleUserNotFound(NotFoundException exception)
    {
        return new ResponseEntity<>(formApiResponseAndLogException(exception), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ActionNotAllowedException.class})
    public ResponseEntity<ApiResponse<String>> handleActionNotAllowed(ActionNotAllowedException exception)
    {
        return new ResponseEntity<>(formApiResponseAndLogException(exception), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {FieldMissingException.class})
    public ResponseEntity<ApiResponse<String>> handleFieldMissing(FieldMissingException exception)
    {
        return new ResponseEntity<>(formApiResponseAndLogException(exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<ApiResponse<String>> handleAccessDenied(AccessDeniedException exception)
    {
        return new ResponseEntity<>(formApiResponseAndLogException(exception), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {HttpClientErrorException.Forbidden.class})
    public ResponseEntity<ApiResponse<String>> handleForbidden(HttpClientErrorException.Forbidden exception)
    {
        return new ResponseEntity<>(formApiResponseAndLogException(exception), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {HttpClientErrorException.NotFound.class})
    public ResponseEntity<ApiResponse<String>> handleNotFound(HttpClientErrorException.NotFound exception)
    {
        return new ResponseEntity<>(formApiResponseAndLogException(exception), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<ApiResponse<String>> handleRunTime(RuntimeException exception)
    {
        return new ResponseEntity<>(formApiResponseAndLogException(exception), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ApiResponse<String> formApiResponseAndLogException(Exception exception)
    {
        logger.error("Exception occurred at endpoint [{}] with method [{}], query parameters [{}], and message: [{}]. Root Cause: {}",
                httpServletRequest.getRequestURI(),
                httpServletRequest.getMethod(),
                httpServletRequest.getQueryString() != null ? httpServletRequest.getQueryString() : "N/A",
                exception.getMessage(),
                exception.getCause() != null ? exception.getCause().toString() : "N/A");

        return ApiResponse.<String>builder().responseType(ResponseType.ERROR).responseMessage(exception.getMessage()).build();
    }

}
