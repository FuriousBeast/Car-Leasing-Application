package com.nk.cars.exception;

/**
 * ActionNotAllowedException.java
 *
 * @author Nandhakumar N 
 * @module com.nk.cars.exception
 * @created Nov 23, 2024
 */
public class ActionNotAllowedException extends RuntimeException
{
    public ActionNotAllowedException(String message) {
        super(message);
    }
}
