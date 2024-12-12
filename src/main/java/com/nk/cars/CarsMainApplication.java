package com.nk.cars;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * CarsMainApplication.java
 *
 * @author Nandhakumar N
 * @module com.nk.cars
 * @created Nov 23, 2024
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.nk.cars"}) //For Junit Test
public class CarsMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarsMainApplication.class, args);
    }

}
