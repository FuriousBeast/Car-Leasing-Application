package com.nk.cars.exception;

/**
 * FieldMissingException.java
 *
 * @author Nandhakumar N 
 * @module com.nk.cars.exception
 * @created Nov 23, 2024
 */
public class FieldMissingException extends RuntimeException
{
    public FieldMissingException(String message) {
        super(message);
    }
}
