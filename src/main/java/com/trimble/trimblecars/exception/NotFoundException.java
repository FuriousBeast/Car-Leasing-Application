package com.trimble.trimblecars.exception;

/**
 * NotFoundException.java
 *
 * @author Nandhakumar N 
 * @module com.trimble.trimblecars.exception
 * @created Nov 23, 2024
 */
public class NotFoundException extends RuntimeException
{
    public NotFoundException(String message) {
        super(message);
    }
}
