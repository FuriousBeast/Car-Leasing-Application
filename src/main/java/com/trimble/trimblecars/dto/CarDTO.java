package com.trimble.trimblecars.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CarDTO.java
 *
 * @author Nandhakumar N 
 * @module com.trimble.trimblecars.entity
 * @created Nov 23, 2024
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CarDTO
{
    private String make;
    private String model;
    private String year;
}
