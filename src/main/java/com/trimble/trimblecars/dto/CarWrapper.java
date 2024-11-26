package com.trimble.trimblecars.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * CarWrapper.java
 *
 * @author Nandhakumar N 
 * @module com.trimble.trimblecars.entity
 * @created Nov 23, 2024
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CarWrapper
{
    private int total_count;

    private List<CarDTO> results;
}
