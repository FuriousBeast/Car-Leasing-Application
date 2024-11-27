package com.trimble.trimblecars.controller;

import com.trimble.trimblecars.entity.Car;
import com.trimble.trimblecars.entity.Role;
import com.trimble.trimblecars.entity.User;
import com.trimble.trimblecars.exception.FieldMissingException;
import com.trimble.trimblecars.service.CarService;
import com.trimble.trimblecars.service.AuthService;
import com.trimble.trimblecars.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CarController.java
 * <p>
 * Handles all car-related operations including fetching, registering, updating,
 *  * and retrieving cars by various filters.
 * @author Nandhakumar N 
 * @module com.trimble.trimblecars.controller
 * @created Nov 23, 2024
 */
@RestController
@RequestMapping("/cars")
public class CarController
{
    @Autowired
    private CarService carService;

    @Autowired
    AuthService authService;

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private UserService userService;

    /**
     * Registers a new car for the authenticated user(CAR_OWNER).
     *
     * @param car     Car details
     * @param ownerId the id of the owner to be registered by admin
     * @return Registered car details
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'CAR_OWNER')")
    @PostMapping("/register")
    public ResponseEntity<Car> registerCar(@RequestBody Car car,
                                           @RequestParam(required = false) Long ownerId)
    {
        User user = authService.fetchUserFromAuth();

        Car registeredCar;

        if(user.getRole().equals(Role.ADMIN))
        {
            if(ownerId == null)
                throw new FieldMissingException("Field 'ownerId' required when process initiated by ADMIN");

            User owner = userService.getUserById(ownerId);

            logger.info("Registering car by admin for user  {} : {}", owner.getEmail(), car);

            registeredCar = carService.registerCar(car, owner);

            logger.info("Car registered successfully by admin for user {}", owner.getEmail());
        }
        else
        {
            logger.info("Registering car for user {} : {}", user.getEmail(), car);

            registeredCar = carService.registerCar(car, user);

            logger.info("Car registered successfully for user {}", user.getEmail());
        }

        return ResponseEntity.ok(registeredCar);
    }

    /**
     * Updates the status of a car.
     *
     * @param carId  ID of the car
     * @param status New status of the car
     * @return Updated car details
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'CAR_OWNER')")
    @PutMapping("/update")
    public ResponseEntity<Car> updateCar(@RequestParam Long carId, @RequestParam String status)
    {
        User user = authService.fetchUserFromAuth();

        logger.info("Updating car for user {} with ID: {} to status: {}", user.getEmail(), carId, status);

        Car updatedCar = carService.updateCar(carId, status, user);

        logger.info("Car updated successfully for user {}",user.getEmail());

        return ResponseEntity.ok(updatedCar);
    }

    /**
     * Fetches the list of all cars (admin-only).
     *
     * @return List of all cars
     */
    @GetMapping("/getAllCars")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Car>> getAllCars()
    {
        logger.info("Request received from Admin for fetching all cars.");

        var fetchedCars = carService.getAllCars();

        logger.info("Successfully fetched cars by admin.. size = {}", fetchedCars.size());

        return ResponseEntity.ok(fetchedCars);
    }

    /**
     * Fetches cars owned by the authenticated user.
     *
     * @return List of cars owned by the user
     */
    @GetMapping("/getCarsByOwner")
    @PreAuthorize("hasAnyRole('ADMIN','CAR_OWNER')")
    public ResponseEntity<List<Car>> getAllCarsByOwner()
    {
        logger.info("Fetching cars owned by the authenticated user.");

        User userFromAuth = authService.fetchUserFromAuth();

        var allCarsByOwner = carService.getAllCarsByOwner(userFromAuth);

        logger.info("Successfully fetched cars owned by the user.. size = {}",allCarsByOwner.size());

        return ResponseEntity.ok(allCarsByOwner);
    }

    /**
     * Fetches all available cars for leasing.
     *
     * @return List of available cars
     */
    @GetMapping("/getAvailableCars")
    public ResponseEntity<List<Car>> getAvailableCars()
    {
        logger.info("Fetching available cars.");

        var availableCars = carService.getAvailableCars();

        logger.info("Successfully fetched available cars.. size = {}", availableCars.size());

        return ResponseEntity.ok(availableCars);
    }

    /**
     * Fetches all cars currently leased.
     *
     * @return List of leased cars
     */
    @GetMapping("/getLeasedCars")
    public ResponseEntity<List<Car>> getLeasedCars()
    {
        logger.info("Fetching leased cars.");

        var leasedCars = carService.getLeasedCars();

        logger.info("Successfully fetched leased cars.. size = {}", leasedCars.size());

        return ResponseEntity.ok(leasedCars);
    }
}
