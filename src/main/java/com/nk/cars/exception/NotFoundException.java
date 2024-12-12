package com.nk.cars.exception;

/**
 * NotFoundException.java
 *
 * @author Nandhakumar N 
 * @module com.nk.cars.exception
 * @created Nov 23, 2024
 */
public class NotFoundException extends RuntimeException
{
    public NotFoundException(String message) {
        super(message);
    }
}
