package com.nk.cars.service;

import com.nk.cars.dto.ApiResponse;
import com.nk.cars.dto.ResponseType;
import com.nk.cars.entity.Role;
import com.nk.cars.entity.User;
import com.nk.cars.exception.ActionNotAllowedException;
import com.nk.cars.exception.FieldMissingException;
import com.nk.cars.exception.NotFoundException;
import com.nk.cars.repo.UserRepository;
import com.nk.cars.utils.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * UserService.java
 * <p>
 * Service class for managing user-related operations.
 * Handles user registration, retrieval, and listing of users
 *
 * @author Nandhakumar N 
 * @module com.nk.cars.service
 * @created Nov 23, 2024
 */
@Service
public class UserService
{
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private UserRepository userRepository;

    /**
     * Registers a new user if the username is not already taken.
     *
     * @param user the user to be registered
     * @return the registered or existing user
     */
    public ApiResponse<User> registerUser(User user)
    {
        logger.info("Attempting to register user with username: {}", user.getUsername());

        //validates user
        validateUser(user);

        // Check if a user with the same email already exists
        User userFromDB = getUserByEmail(user.getEmail());

        if(userFromDB != null)
        {
            logger.warn("Username {} already exists. Returning the existing user.", user.getUsername());
            return ApiResponse.<User>builder()
                    .responseType(ResponseType.WARN)
                    .responseMessage("Username "+ user.getUsername() +" already exists")
                    .returnResponse(userFromDB)
                    .build();
        }

        User savedUser = userRepository.save(user);
        logger.info("User with username {} registered successfully. User ID: {}", savedUser.getUsername(), savedUser.getUserId());

        return ApiResponse.<User>builder()
                .responseType(ResponseType.SUCCESS)
                .responseMessage("User with username " + savedUser.getUsername() + " registered successfully")
                .returnResponse(savedUser)
                .build();
    }

    /**
     * @throws FieldMissingException if required field is missing
    */
    private void validateUser(User user)
    {
        if(CommonUtils.nullOrEmpty(user.getUsername()))
            throw new FieldMissingException("Field 'username' is required!");

        if(CommonUtils.nullOrEmpty(user.getEmail()))
            throw new FieldMissingException("Field 'email' is required!");
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user
     * @return the user object if found
     * @throws NotFoundException if the user is not found
     */
    public User getUserById(Long id)
    {
        logger.info("Fetching user with ID: {}", id);

        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with ID " + id +" not found"));
    }

    public User getUserByEmail(String email)
    {
        return userRepository.findByEmail(email);
    }

    /**
     * Retrieves a list of all registered users.
     *
     * @return a list of all users
     */
    public List<User> getUsers()
    {
        logger.info("Fetching all registered users");

        return userRepository.findAll();
    }

    public User updateUser(String email, String role)
    {
        try
        {
            User userByEmail = getUserByEmail(email);

            if(userByEmail == null) {
                throw new NotFoundException("User with email id "+email + " not found in records");
            }

            if(userByEmail.getRole() != Role.ROLE_PENDING) {
                throw new ActionNotAllowedException("User with email id "+email + " is already a "+userByEmail.getRole() + " Role change is not applicable");
            }

            Role userRole = Role.valueOf(role);

            userByEmail.setRole(userRole);

            return userRepository.save(userByEmail);
        }
        catch (IllegalArgumentException e)
        {
            throw new FieldMissingException("Field role can be (ADMIN/CAR_OWNER/CUSTOMER)");
        }
    }
}
