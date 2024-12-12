package com.nk.cars.controller;

import com.nk.cars.entity.Lease;
import com.nk.cars.entity.Role;
import com.nk.cars.entity.User;
import com.nk.cars.exception.FieldMissingException;
import com.nk.cars.service.AuthService;
import com.nk.cars.service.LeaseService;
import com.nk.cars.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * LeaseController.java
 *
 * <p>
 * Controller for managing lease-related API endpoints.
 * Handles lease creation, termination, fetching history, and exporting history as PDF.
 * </p>
 *
 * @author Nandhakumar N 
 * @module com.nk.cars.controller
 * @created Nov 23, 2024
 */

@RestController
@RequestMapping("/lease")
public class LeaseController
{
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private LeaseService leaseService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    /**
     * Starts a new lease for a given car and user.
     *
     * @param carId  the ID of the car to lease
     * @param userId the ID of the user to start lease by admin
     * @return a response entity containing the created lease
     */
    @PostMapping("/startLease")
    @PreAuthorize("hasAnyRole('ADMIN','END_CUSTOMER')")
    public ResponseEntity<Lease> startLease(@RequestParam Long carId,
                                            @RequestParam(required = false) Long userId)
    {
        User user = authService.fetchUserFromAuth();

        Lease lease;

        if(user.getRole() == Role.ADMIN)
        {
            if(userId == null)
                throw new FieldMissingException("Field 'ownerId' required when process initiated by ADMIN");

            User customer = userService.getUserById(userId);

            logger.info("Received request to start lease by admin for carId: {} and customer : {}", carId, customer.getEmail());

            lease = leaseService.startLease(carId, customer);

            logger.info("Lease started successfully by admin with ID: {}", lease.getId());
        }

        else
        {
            logger.info("Received request to start lease for carId: {} by user : {}", carId, user.getEmail());

            lease = leaseService.startLease(carId, user);

            logger.info("Lease started successfully with ID: {}", lease.getId());
        }

        return ResponseEntity.ok(lease);
    }

    /**
     * Ends an existing lease for a given lease ID and user.
     *
     * @param leaseId the ID of the lease to terminate
     * @return a response entity containing the updated lease
     */
    @PreAuthorize("hasAnyRole('ADMIN','END_CUSTOMER')")
    @PostMapping("/endLease")
    public ResponseEntity<Lease> endLease(@RequestParam Long leaseId,
                                          @RequestParam(required = false) Long userId)
    {
        User user = authService.fetchUserFromAuth();

        Lease lease;

        if(user.getRole() == Role.ADMIN)
        {
            if(userId == null)
                throw new FieldMissingException("Field 'userId' required when process initiated by ADMIN");

            User customer = userService.getUserById(userId);

            logger.info("Received request to end lease by admin with ID: {} for user: {}", leaseId, customer.getEmail());

            lease = leaseService.endLease(leaseId, customer);

            logger.info("Lease with ID: {} ended successfully by admin", lease.getId());
        }

        else
        {
            logger.info("Received request to end lease with ID: {} by userId: {}", leaseId, user.getEmail());

            lease = leaseService.endLease(leaseId, user);

            logger.info("Lease with ID: {} ended successfully", lease.getId());
        }

        return ResponseEntity.ok(lease);
    }

    /**
     * Fetches lease details for a given lease ID.
     *
     * @param leaseId the ID of the lease to retrieve
     * @return a response entity containing the lease details
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getLease")
    public ResponseEntity<Lease> getLeaseById(@RequestParam Long leaseId)
    {
        logger.info("Received request for fetching lease details for leaseId: {}", leaseId);

        Lease lease = leaseService.getLeaseById(leaseId);

        logger.info("Lease found {}", lease);

        return ResponseEntity.ok(lease);
    }

    /**
     * Fetches lease history for a specific user or all users.
     *
     * @return a response entity containing the lease history
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'END_CUSTOMER')")
    @GetMapping("/getAllHistory")
    public ResponseEntity<List<Lease>> getAllHistory()
    {
        logger.info("Received request for fetching lease details");

        User user = authService.fetchUserFromAuth();

        List<Lease> leaseHistory;

        if(user.getRole().equals(Role.ADMIN))
        {
            logger.info("Fetching lease history for all users..");
            leaseHistory = leaseService.getAllLeaseHistory();
            logger.info("Fetched {} total lease records", leaseHistory.size());

        }
        else
        {
            logger.info("Fetching lease history for user emailId {}", user.getEmail());

            leaseHistory = leaseService.getLeaseHistoryForUser(user);

            logger.info("Fetched {} lease records for user : {}", leaseHistory.size(), user.getEmail());
        }

        logger.info("Fetched lease history size = {}", leaseHistory.size());
        return ResponseEntity.ok(leaseHistory);
    }

    /**
     * Exports lease history for a specific user or all users as a PDF.
     *
     * @return a response entity containing the PDF file
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'END_CUSTOMER')")
    @GetMapping("/getAllHistoryAsPDF")
    public ResponseEntity<byte[]> exportLeaseHistoryAsPDF()
    {
        byte[] pdfAsBytes;

        logger.info("Received request for exporting lease details as pdf");

        User user = authService.fetchUserFromAuth();

        String fileName;

        String email = user.getEmail();

        if(user.getRole().equals(Role.ADMIN))
        {
            logger.info("Exporting all lease history...");
            fileName = "lease_history_all.pdf";
            pdfAsBytes = leaseService.getLeaseHistoryAsPDF();
        }
        else
        {
            logger.info("Exporting lease history for user: {}", user.getEmail());
            fileName = "lease_history_" + email +".pdf";
            pdfAsBytes = leaseService.getLeaseHistoryForUserAsPDF(user);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .body(pdfAsBytes);
    }

}
