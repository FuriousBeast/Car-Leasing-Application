package com.trimble.trimblecars.exception;

/**
 * ActionNotAllowedException.java
 *
 * @author Nandhakumar N 
 * @module com.trimble.trimblecars.exception
 * @created Nov 23, 2024
 */
public class ActionNotAllowedException extends RuntimeException
{
    public ActionNotAllowedException(String message) {
        super(message);
    }
}
