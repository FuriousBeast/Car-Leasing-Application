package com.nk.cars.controller;

import com.nk.cars.entity.User;
import com.nk.cars.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UserController.java
 *
 * <p>
 *     Controller for managing user-related API endpoints.
 *     Handles user registration and retrieval operations.
 * </p>
 * @author Nandhakumar N 
 * @module com.nk.cars.controller
 * @created Nov 23, 2024
 */
@RestController
@RequestMapping("/user")
public class UserController
{
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private UserService userService;

    /**
     * Retrieves all registered users.
     *
     * @return a response entity containing a list of all users
     */
    @GetMapping("/getAllUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers()
    {
        logger.info("Received request to fetch all users");

        List<User> users = userService.getUsers();

        logger.info("Fetched {} users successfully", users.size());

        return ResponseEntity.ok(users);
    }
}
