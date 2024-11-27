package com.trimble.trimblecars.api_integration.controller;

import com.trimble.trimblecars.api_integration.service.OpenDataSoftApiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * OpenDataSoftController.java
 *
 * @author Nandhakumar N (nandhakumarn@nmsworks.co.in)
 * @module com.trimble.trimblecars.api_integration.controller
 * @created Nov 26, 2024
 */
@RestController
@RequestMapping("/api")
public class OpenDataSoftController
{
    @Autowired
    private OpenDataSoftApiService openDataSoftApiService;

    private static final Logger logger = LogManager.getLogger();

    /**
     * Fetches the list of all car brands from public.opendatasoft.com
     * Endpoint: GET /cars/getBrands.
     *
     * @see <a href="https://shorturl.at/xZiTs">Api Documentation</a>
     * @return Set of car brands
     */
    @GetMapping("/getBrands")
    public ResponseEntity<Set<String>> getListOfBrands()
    {
        logger.info("Fetching list of car brands.");

        var listOfBrands = openDataSoftApiService.getListOfBrands();

        logger.info("Successfully fetched list of car brands.");

        return ResponseEntity.ok(listOfBrands);
    }

    /**
     * Fetches the list of car models for a given brand.
     *
     * @param make The car brand
     * @return Set of car models
     */
    @GetMapping("/getModel")
    public ResponseEntity<Set<String>> getListOfBrands(@RequestParam String make)
    {
        logger.info("Fetching list of car models for brand: {}", make);

        var listOfBrands = openDataSoftApiService.getListOfModels(make);
        
        logger.info("Successfully fetched car models for brand: {}", make);

        return ResponseEntity.ok(listOfBrands);
    }
}
