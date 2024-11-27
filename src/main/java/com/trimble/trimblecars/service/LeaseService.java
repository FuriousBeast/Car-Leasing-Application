package com.trimble.trimblecars.service;

import com.trimble.trimblecars.entity.*;
import com.trimble.trimblecars.exception.ActionNotAllowedException;
import com.trimble.trimblecars.exception.NotFoundException;
import com.trimble.trimblecars.repo.LeaseRepository;
import com.trimble.trimblecars.utils.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * LeaseService.java
 * <p>
 * Service class for managing car lease operations.
 * Handles lease start, end, and history functionalities while applying validation rules.
 * @author Nandhakumar N 
 * @module com.trimble.trimblecars.service
 * @created Nov 23, 2024
 */

@Service
public class LeaseService
{
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private LeaseRepository leaseRepository;

    @Autowired
    private CarService carService;

    @Autowired
    private ReportService reportService;


    /**
     * Starts a lease for the specified car and user.
     *
     * @param carId  the ID of the car to lease
     * @param user the user starting the lease
     * @return the created Lease object
     */
    public Lease startLease(Long carId, User user)
    {
        logger.info("Starting lease for carId: {} by userId: {}", carId, user);

        Car car = carService.getCarById(carId);

        String email = user.getEmail();

        if(user.getRole().equals(Role.CAR_OWNER))
        {
            throw new ActionNotAllowedException("User with ID " + email + " is a car owner and cannot start a lease");
        }

        List<Lease> leases = user.getLeases();

        // Validate user lease limits
        if(validateActiveLease(leases))
        {
            throw new ActionNotAllowedException("User with ID " + email + " has already leased 2 cars");
        }

        // Validate car availability
        if(!car.getStatus().equals(Status.IDLE))
        {
            throw new ActionNotAllowedException("Car with ID " + carId +" is not available for leasing. Current status : " +car.getStatus());
        }

        // Create a new lease
        Lease lease = new Lease();
        lease.setCar(car);
        lease.setCustomer(user);
        lease.setStartDate(LocalDateTime.now());

        // Update car status to ON_LEASE
        car.setStatus(Status.ON_LEASE);
        carService.saveCar(car);

        logger.info("Lease started successfully for carId: {} by userId: {}", carId, email);
        return leaseRepository.save(lease);
    }

    /**
     * Ends a lease for the specified lease ID and user.
     *
     * @param leaseId the ID of the lease to end
     * @param user  the user ending the lease
     * @return the updated Lease object
     */
    public Lease endLease(Long leaseId, User user)
    {
        String email = user.getEmail();

        logger.info("Ending lease with ID: {} for {}", leaseId, email);

        Lease lease = getLeaseById(leaseId);

        logger.debug("Lease retrieved: {}, User found: {}", lease, user);

        if(!isValidUser(lease, user))
        {
            throw new ActionNotAllowedException("User with ID " + email + " is not authorized to end lease ID "+ leaseId);
        }

        if(lease.getState().equals(State.ENDED))
        {
            throw new ActionNotAllowedException("Lease with ID " + leaseId + " already ended");
        }

        // End the lease and update car status
        Car car = lease.getCar();
        car.setStatus(Status.IDLE);

        lease.setEndDate(LocalDateTime.now());

        carService.saveCar(car);

        logger.info("Lease with ID: {} ended successfully for userId: {}", leaseId, email);

        return leaseRepository.save(lease);
    }

    /**
     * Validates if the user is authorized to manage the lease.
     *
     * @param lease the lease object
     * @param user  the user attempting the action
     * @return true if the user is authorized, false otherwise
     */
    private boolean isValidUser(Lease lease, User user)
    {
        return user.getUserId().equals(lease.getCustomer().getUserId()) || user.getRole().equals(Role.ADMIN);
    }

    /**
     * Retrieves the lease history for a specific user.
     *
     * @param user the user object
     * @return a list of Lease objects associated with the user
     */
    public List<Lease> getLeaseHistoryForUser(User user)
    {
        logger.info("Fetching lease history for email: {}", user.getEmail());

        if(user.getRole().equals(Role.CAR_OWNER))
        {
            throw new ActionNotAllowedException("Car owner (Email ID: " + user.getEmail() + ") can't have lease history");
        }

        return leaseRepository.findAllByCustomer(user);
    }

    /**
     * Retrieves the complete lease history.
     *
     * @return a list of all Lease objects
     */
    public List<Lease> getAllLeaseHistory()
    {
        logger.info("Fetching all lease history");
        return leaseRepository.findAll();
    }

    /**
     * Retrieves a specific lease by its ID.
     *
     * @param leaseId the ID of the lease
     * @return the Lease object
     */
    public Lease getLeaseById(Long leaseId)
    {
        logger.info("Fetching lease with ID: {}", leaseId);

        return leaseRepository.findById(leaseId)
                .orElseThrow(() -> new NotFoundException("Lease data not found in records"));
    }

    private boolean validateActiveLease(List<Lease> leases)
    {
        if(CommonUtils.nullOrEmpty(leases))
            return false;
        return leases
                .stream()
                .filter(lease -> lease.getState().equals(State.ACTIVE))
                .count() >= 2;
    }

    public byte[] getLeaseHistoryAsPDF()
    {
        return reportService.generateLeaseHistoryReport(getAllLeaseHistory());
    }

    public byte[] getLeaseHistoryForUserAsPDF(User user)
    {
        return reportService.generateLeaseHistoryReport(getLeaseHistoryForUser(user));
    }
}
