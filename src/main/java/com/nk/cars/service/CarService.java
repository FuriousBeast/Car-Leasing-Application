package com.nk.cars.service;

import com.nk.cars.entity.Car;
import com.nk.cars.entity.Role;
import com.nk.cars.entity.Status;
import com.nk.cars.entity.User;
import com.nk.cars.entity.*;
import com.nk.cars.exception.ActionNotAllowedException;
import com.nk.cars.exception.FieldMissingException;
import com.nk.cars.exception.NotFoundException;
import com.nk.cars.repo.CarRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * CarService.java
 *
 * <p>
 * Service layer for managing car-related operations, including registering, updating, and querying cars.
 * Ensures validation and business rules are applied.
 * </p>
 * @author Nandhakumar N 
 * @module com.nk.cars.service
 * @created Nov 23, 2024
 */
@Service
public class CarService
{
    private static final Logger log = LogManager.getLogger();

    @Autowired
    private CarRepository carRepository;

    /**
     * Registers a car with the given owner ID.
     *
     * @param car     the car to be registered
     * @param user    the owner registering the car
     * @return the registered car
     */
    public Car registerCar(Car car, User user)
    {
        log.info("Registering car with license plate: {} for owner ID: {}", car.getLicensePlateNumber(), user.getEmail());

        if(user.getRole().equals(Role.END_CUSTOMER))
        {
            throw new ActionNotAllowedException("Customer (Email ID: " + user.getEmail() +") cannot register cars");
        }

        car.setOwner(user);
        return registerCar(car);
    }

    /**
     * Validates and registers a car.
     *
     * @param car the car to be registered
     * @return the registered car
     */
    public Car registerCar(Car car)
    {
        validateCar(car);
        validateDuplicateEntry(car);
        log.info("Car with license plate: {} successfully validated", car.getLicensePlateNumber());
        return carRepository.save(car);
    }

    public Car saveCar(Car car)
    {
        return carRepository.save(car);
    }

    /**
     * Updates the status of an existing car.
     *
     * @param carId  the ID of the car to update
     * @param status the new status of the car
     * @param user the owner of the car
     * @return the updated car
     */
    public Car updateCar(Long carId, String status, User user)
    {
        Status carStatus = Status.valueOf(status);

        log.info("Updating car ID: {} to status: {}", carId, status);

        log.info("Before calling get Cars");

        Car carFromDB = getCarById(carId);

        log.info("Get cars from DB {}", carFromDB);

        if(!Objects.equals(carFromDB.getOwner().getUserId(), user.getUserId()))
        {
            throw new ActionNotAllowedException("Attempted to set car ID: " + carId + " where owner differs, which is not allowed");
        }

        if(carStatus == Status.ON_LEASE)
        {
            throw new ActionNotAllowedException("Attempted to set car ID: " + carId +" to ON_LEASE directly, which is not allowed");
        }


        if(carFromDB.getStatus().equals(Status.ON_LEASE))
        {
            throw new ActionNotAllowedException("Attempted to set car ID: " + carId + " which is ON_LEASE to ON_SERVICE directly, which is not allowed");
        }

        if(carFromDB.getStatus().equals(carStatus))
        {
            log.info("Car ID: {} is already in the desired status: {}", carId, status);
            return carFromDB;
        }

        carFromDB.setStatus(carStatus);

        return saveCar(carFromDB);
    }

    /**
     * Fetches all cars owned by a specific owner.
     *
     * @param user the owner
     * @return list of cars owned by the user
     */
    public List<Car> getAllCarsByOwner(User user)
    {
        log.info("Fetching all cars for emailID: {}", user.getEmail());

        if(user.getRole().equals(Role.END_CUSTOMER))
        {
            throw new ActionNotAllowedException("Access denied: Customer (ID: " + user.getEmail() + ") cannot fetch owned cars");
        }

        return carRepository.findAllByOwner(user);
    }

    /**
     * Fetches all cars available for lease.
     *
     * @return list of cars in IDLE status
     */
    public List<Car> getAvailableCars()
    {
        log.info("Fetching all available cars for lease");

        return getAllCars().stream().filter(car -> car.getStatus() == Status.IDLE).toList();
    }

    /**
     * Fetches all cars currently on lease.
     *
     * @return list of cars in ON_LEASE status
     */
    public List<Car> getLeasedCars()
    {
        log.info("Fetching all leased cars");

        return getAllCars().stream().filter(car -> car.getStatus() == Status.ON_LEASE).toList();
    }

    /**
    * @throws FieldMissingException if required fields are missing
    * */
    private void validateCar(Car car)
    {
        if(car.getMake() == null)
            throw new FieldMissingException("Field 'make' is required!");

        if(car.getModel() == null)
            throw new FieldMissingException("Field 'model' is required!");

        if(car.getYear() == 0)
            throw new FieldMissingException("Field 'year' is required!");

        if(car.getLicensePlateNumber() == null)
            throw new FieldMissingException("Field 'carnumber' is required!");
    }

    /**
     * @throws ActionNotAllowedException if any duplicate registration occurs
    * */
    private void validateDuplicateEntry(Car car)
    {
        boolean isCarExists = carRepository.existsCarByLicensePlateNumber(car.getLicensePlateNumber());

        if(isCarExists) {
            throw new ActionNotAllowedException("Car with number '" + car.getLicensePlateNumber() +"' already registered");
        }
    }

    /**
     * Fetched car with given id
     *
     * @param id The id of the car to be fetched
     * @throws NotFoundException if not found in records
    * */
    public Car getCarById(Long id)
    {
        return carRepository.findById(id).orElseThrow(() -> new NotFoundException("Car with id " + id + " is not found in records"));
    }

    /**
     * Fetched all cars from DB
    * */
    public List<Car> getAllCars()
    {
        return carRepository.findAll();
    }
}
