package com.trimble.trimblecars.exception;

/**
 * FieldMissingException.java
 *
 * @author Nandhakumar N 
 * @module com.trimble.trimblecars.exception
 * @created Nov 23, 2024
 */
public class FieldMissingException extends RuntimeException
{
    public FieldMissingException(String message) {
        super(message);
    }
}
