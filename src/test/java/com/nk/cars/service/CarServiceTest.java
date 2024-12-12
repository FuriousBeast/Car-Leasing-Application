package com.nk.cars.service;

import com.nk.cars.entity.Car;
import com.nk.cars.entity.Role;
import com.nk.cars.entity.Status;
import com.nk.cars.entity.User;
import com.nk.cars.exception.ActionNotAllowedException;
import com.nk.cars.repo.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * CarServiceTest.java
 *
 * @author Nandhakumar N 
 * @module com.nk.cars.service
 * @created Nov 24, 2024
 */
public class CarServiceTest {

    @InjectMocks
    private CarService carService;

    @Mock
    private CarRepository carRepository;

    @Mock
    private Car testCar;

    @Mock
    private User testOwner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerCar_Success()
    {
        when(carRepository.existsCarByLicensePlateNumber("ABC123")).thenReturn(false);
        when(carRepository.save(testCar)).thenReturn(testCar);
        when(testOwner.getRole()).thenReturn(Role.CAR_OWNER);
        when(testCar.getMake()).thenReturn("Mahindra");
        when(testCar.getModel()).thenReturn("Thar");
        when(testCar.getLicensePlateNumber()).thenReturn("TN 72 NR 2344");
        when(testCar.getYear()).thenReturn(2022);

        Car savedCar = carService.registerCar(testCar, testOwner);

        assertNotNull(savedCar);
        verify(carRepository, times(1)).save(testCar);
    }

    @Test
    void registerCar_CustomerNotAllowed()
    {
        when(testOwner.getRole()).thenReturn(Role.END_CUSTOMER);

        assertThrows(ActionNotAllowedException.class, () -> carService.registerCar(testCar, testOwner));
    }

    @Test
    void updateCarStatus_Success()
    {
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Mock save to return the updated object
        when(testOwner.getUserId()).thenReturn(1L);
        when(testCar.getOwner()).thenReturn(testOwner);
        when(testCar.getStatus()).thenReturn(Status.IDLE);

        Car updatedCar = carService.updateCar(1L, "ON_SERVICE", testOwner);

        when(testCar.getStatus()).thenReturn(Status.ON_SERVICE);

        assertEquals(Status.ON_SERVICE, updatedCar.getStatus());
        verify(carRepository, times(1)).save(testCar);
    }
}
