package com.trimble.trimblecars.service;

import com.trimble.trimblecars.entity.*;
import com.trimble.trimblecars.exception.ActionNotAllowedException;
import com.trimble.trimblecars.exception.NotFoundException;
import com.trimble.trimblecars.repo.LeaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LeaseServiceTest {

    @InjectMocks
    private LeaseService leaseService;

    @Mock
    private LeaseRepository leaseRepository;

    @Mock
    private CarService carService;

    private Car testCar;
    private User testUser;
    private Lease testLease;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCar = new Car();
        testCar.setId(1L);
        testCar.setStatus(Status.IDLE);

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setRole(Role.END_CUSTOMER);

        testLease = new Lease();
        testLease.setId(1L);
        testLease.setCar(testCar);
        testLease.setCustomer(testUser);
        testLease.setStartDate(LocalDateTime.now());
        testLease.setState(State.ACTIVE);
    }

    @Test
    void startLease_Success() {
        // Arrange
        when(carService.getCarById(1L)).thenReturn(testCar);
        when(leaseRepository.save(any(Lease.class))).thenReturn(testLease);
//        when(testUser.getLeases()).thenReturn(new ArrayList<>());

        // Act
        Lease lease = leaseService.startLease(1L, testUser);

        // Assert
        assertNotNull(lease);
        assertEquals(State.ACTIVE, lease.getState());
        verify(carService, times(1)).saveCar(testCar);
        verify(leaseRepository, times(1)).save(any(Lease.class));
    }

    @Test
    void startLease_CarNotIdle() {
        // Arrange
        testCar.setStatus(Status.ON_LEASE);
        when(carService.getCarById(1L)).thenReturn(testCar);

        // Act & Assert
        ActionNotAllowedException exception = assertThrows(ActionNotAllowedException.class,
                () -> leaseService.startLease(1L, testUser));
        assertEquals("Car with ID " + 1L +" is not available for leasing. Current status : "+ testCar.getStatus(), exception.getMessage());
        verify(leaseRepository, never()).save(any(Lease.class));
    }

    @Test
    void startLease_UserAlreadyLeasedTwoCars() {
        // Arrange
        List<Lease> leases = List.of(testLease, testLease);
        when(carService.getCarById(1L)).thenReturn(testCar);
        testUser.setLeases(leases);

        // Act & Assert
        assertThrows(ActionNotAllowedException.class,
                () -> leaseService.startLease(1L, testUser));

        verify(leaseRepository, never()).save(any(Lease.class));
    }

    @Test
    void endLease_Success() {
        // Arrange
        when(leaseRepository.findById(1L)).thenReturn(Optional.of(testLease));
        when(leaseRepository.save(any(Lease.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Lease endedLease = leaseService.endLease(1L, testUser);

        // Assert
        assertNotNull(endedLease);
        assertEquals(State.ENDED, endedLease.getState());
        assertEquals(Status.IDLE, testCar.getStatus());
        verify(carService, times(1)).saveCar(testCar);
        verify(leaseRepository, times(1)).save(testLease);
    }

    @Test
    void endLease_LeaseAlreadyEnded() {
        // Arrange
        testLease.setState(State.ENDED);
        when(leaseRepository.findById(1L)).thenReturn(Optional.of(testLease));

        // Act & Assert
        assertThrows(ActionNotAllowedException.class,
                () -> leaseService.endLease(1L, testUser));

        verify(carService, never()).saveCar(any());
    }

    @Test
    void endLease_UnauthorizedUser() {
        // Arrange
        User unauthorizedUser = new User();
        unauthorizedUser.setUserId(2L);
        unauthorizedUser.setEmail("sample@mail");
        unauthorizedUser.setRole(Role.END_CUSTOMER);

        when(leaseRepository.findById(1L)).thenReturn(Optional.of(testLease));

        // Act & Assert
        ActionNotAllowedException exception = assertThrows(ActionNotAllowedException.class,
                () -> leaseService.endLease(1L, unauthorizedUser));
        assertEquals("User with ID " + unauthorizedUser.getEmail() + " is not authorized to end lease ID "+ 1L, exception.getMessage());
        verify(carService, never()).saveCar(any());
    }

    @Test
    void getLeaseHistoryForUser_Success() {
        // Arrange
        when(leaseRepository.findAllByCustomer(testUser)).thenReturn(List.of(testLease));

        // Act
        List<Lease> leases = leaseService.getLeaseHistoryForUser(testUser);

        // Assert
        assertNotNull(leases);
        assertEquals(1, leases.size());
        verify(leaseRepository, times(1)).findAllByCustomer(testUser);
    }

    @Test
    void getLeaseHistoryForUser_CarOwnerNotAllowed() {
        // Arrange
        testUser.setRole(Role.CAR_OWNER);

        // Act & Assert
        assertThrows(ActionNotAllowedException.class,
                () -> leaseService.getLeaseHistoryForUser(testUser));

        verify(leaseRepository, never()).findAllByCustomer(any());
    }

    @Test
    void getLeaseById_Success() {
        // Arrange
        when(leaseRepository.findById(1L)).thenReturn(Optional.of(testLease));

        // Act
        Lease lease = leaseService.getLeaseById(1L);

        // Assert
        assertNotNull(lease);
        assertEquals(1L, lease.getId());
        verify(leaseRepository, times(1)).findById(1L);
    }

    @Test
    void getLeaseById_NotFound() {
        // Arrange
        when(leaseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> leaseService.getLeaseById(999L));
        assertEquals("Lease data not found in records", exception.getMessage());
        verify(leaseRepository, times(1)).findById(999L);
    }
}
