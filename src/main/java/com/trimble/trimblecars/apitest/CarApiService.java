package com.trimble.trimblecars.apitest;

import com.trimble.trimblecars.dto.CarDTO;
import com.trimble.trimblecars.dto.CarWrapper;
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
 * @module com.trimble.trimblecars.apitest
 * @created Nov 23, 2024
 */

@Component
public class CarApiService
{
    @Value("${api.baseurl}")
    String baseUrl;

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final Map<String, Object> params = new HashMap<>();

    static
    {
        params.put("select", "make,model,year");
    }

    public Set<String> getListOfBrands()
    {
        params.put("select", "make");
        ResponseEntity<CarWrapper> entity = restTemplate.getForEntity(baseUrl + "&select={select}", CarWrapper.class, params);
        if(entity.getStatusCode().is2xxSuccessful())
        {
            CarWrapper carWrapper = entity.getBody();
            if(carWrapper == null)
                return Collections.emptySet();
            return carWrapper.getResults()
                    .stream()
                    .map(CarDTO::getMake)
                    .collect(Collectors.toCollection(TreeSet::new));
        }
        else
            return Collections.emptySet();
    }

    public Set<String> getListOfModels(String make)
    {
        Map<String, String> map = new HashMap<>();

        map.put("select", "model");
        map.put("where", "make like \""+make+"\"");
        ResponseEntity<CarWrapper> entity = restTemplate.getForEntity(baseUrl + "&select={select}&where={where}", CarWrapper.class, map);

        if(entity.getStatusCode().is2xxSuccessful())
        {
            CarWrapper carWrapper = entity.getBody();

            if(carWrapper == null)
                return Collections.emptySet();

            return carWrapper.getResults()
                    .stream()
                    .map(CarDTO::getModel)
                    .collect(Collectors.toCollection(TreeSet::new));
        }
        else
            //TODO handler exception
            return Collections.emptySet();
    }

}
