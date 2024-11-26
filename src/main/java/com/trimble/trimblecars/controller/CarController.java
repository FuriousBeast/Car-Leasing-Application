package com.trimble.trimblecars.controller;

import com.trimble.trimblecars.apitest.CarApiService;
import com.trimble.trimblecars.entity.Car;
import com.trimble.trimblecars.entity.User;
import com.trimble.trimblecars.service.CarService;
import com.trimble.trimblecars.service.AuthService;
import com.trimble.trimblecars.utils.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * CarController.java
 *
 * @author Nandhakumar N 
 * @module com.trimble.trimblecars.controller
 * @created Nov 23, 2024
 */
@RestController
@RequestMapping("/cars")
public class CarController
{
    @Autowired
    private CarApiService carApiService;

    @Autowired
    private CarService carService;

    @Autowired
    AuthService authService;

    private static final Logger logger = LogManager.getLogger();

    @GetMapping("/getBrands")
    public ResponseEntity<Set<String>> getListOfBrands()
    {
        var listOfBrands = carApiService.getListOfBrands();
        return CommonUtils.nullOrEmpty(listOfBrands) ? ResponseEntity.ok(listOfBrands) : ResponseEntity.accepted().body(listOfBrands);
    }

    @GetMapping("/getModel")
    public ResponseEntity<Set<String>> getListOfBrands(@RequestParam String make)
    {
        var listOfBrands = carApiService.getListOfModels(make);
        return CommonUtils.nullOrEmpty(listOfBrands) ? ResponseEntity.ok(listOfBrands) : ResponseEntity.accepted().body(listOfBrands);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CAR_OWNER')")
    @PostMapping("/register")
    public ResponseEntity<Car> registerCar(@RequestBody Car car)
    {
        User user = authService.fetchUserFromAuth();
        return ResponseEntity.ok(carService.registerCar(car, user));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CAR_OWNER')")
    @PutMapping("/update")
    public ResponseEntity<Car> updateCar(@RequestParam Long carId, @RequestParam String status)
    {
        User user = authService.fetchUserFromAuth();
        return ResponseEntity.ok(carService.updateCar(carId, status, user));
    }

    @GetMapping("/getAllCars")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Car>> getAllCars()
    {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @GetMapping("/getCarsByOwner")
    @PreAuthorize("hasAnyRole('ADMIN','CAR_OWNER')")
    public ResponseEntity<List<Car>> getAllCarsByOwner()
    {
        User userFromAuth = authService.fetchUserFromAuth();
        return ResponseEntity.ok(carService.getAllCarsByOwner(userFromAuth));
    }

    @GetMapping("/getAvailableCars")
    public ResponseEntity<List<Car>> getAvailableCars()
    {
        return ResponseEntity.ok(carService.getAvailableCars());
    }

    @GetMapping("/getLeasedCars")
    public ResponseEntity<List<Car>> getLeasedCars()
    {
        return ResponseEntity.ok(carService.getLeasedCars());
    }
}
