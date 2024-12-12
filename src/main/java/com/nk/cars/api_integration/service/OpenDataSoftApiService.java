package com.nk.cars.api_integration.service;

import com.nk.cars.api_integration.dto.CarDTO;
import com.nk.cars.api_integration.dto.CarWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CarApiTest.java
 *
 * @author Nandhakumar N 
 * @module com.nk.cars.apitest
 * @created Nov 23, 2024
 */

@Component
public class OpenDataSoftApiService
{
    @Value("${api.baseurl}")
    String baseUrl;

    private static final Logger logger = LogManager.getLogger();

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final Map<String, Object> params = new HashMap<>();

    static
    {
        params.put("select", "make,model,year");
    }

    public Set<String> getListOfBrands()
    {
        try
        {
            params.put("select", "make");

            ResponseEntity<CarWrapper> entity = restTemplate.getForEntity(baseUrl + "&select={select}", CarWrapper.class, params);

            if(entity.getStatusCode().is2xxSuccessful())
            {
                CarWrapper carWrapper = entity.getBody();
                if(carWrapper == null) {
                    return Collections.emptySet();
                }
                return carWrapper.getResults()
                        .stream()
                        .map(CarDTO::getMake)
                        .collect(Collectors.toCollection(TreeSet::new));
            }
            else
                return Collections.emptySet();
        }
        catch (Exception e)
        {
            logger.error("Exception while fetching list of brands",e);
            throw new RuntimeException(e);
        }
    }

    public List<CarDTO> getListOfModels(String make)
    {
        try {

            params.put("select", "model,year");
            params.put("where", "make like \""+make+"\"");

            ResponseEntity<CarWrapper> entity = restTemplate.getForEntity(baseUrl + "&select={select}&where={where}", CarWrapper.class, params);

            if(entity.getStatusCode().is2xxSuccessful())
            {
                CarWrapper carWrapper = entity.getBody();

                if(carWrapper == null) {
                    return Collections.emptyList();
                }

                return carWrapper.getResults();
            }
            else
                return Collections.emptyList();
        }
        catch (Exception e)
        {
            logger.error("Exception occurred while fetching list of models for make = {}", make, e);
            throw new RuntimeException(e);
        }
    }

}
