package com.nk.cars.api_integration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CarDTO.java
 *
 * @author Nandhakumar N 
 * @module com.nk.cars.entity
 * @created Nov 23, 2024
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarDTO
{
    private String make;
    private String model;
    private String year;
}
