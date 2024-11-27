package com.trimble.trimblecars.service;

import com.trimble.trimblecars.dto.ApiResponse;
import com.trimble.trimblecars.entity.User;
import com.trimble.trimblecars.exception.NotFoundException;
import com.trimble.trimblecars.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UserServiceTest.java
 *
 * @author Nandhakumar N 
 * @module com.trimble.trimblecars.service
 * @created Nov 24, 2024
 */
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setName("john_doe");
        testUser.setEmail("sample@mail.com");
    }

    @Test
    void registerUser_NewUser() {
        when(userRepository.findByName("john_doe")).thenReturn(new ArrayList<>());
        when(userRepository.save(testUser)).thenReturn(testUser);

        ApiResponse<User> userApiResponse = userService.registerUser(testUser);

        assertNotNull(userApiResponse.getReturnResponse());
        assertEquals("john_doe", userApiResponse.getReturnResponse().getName());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void registerUser_ExistingUser()
    {
        when(userRepository.findByName("john_doe")).thenReturn(Collections.singletonList(testUser));
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        ApiResponse<User> userApiResponse = userService.registerUser(testUser);

        User savedUser = userApiResponse.getReturnResponse();
        assertNotNull(savedUser);
        assertEquals("john_doe", savedUser.getName());

        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getUserId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void getUsers() {
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> userList = userService.getUsers();

        assertNotNull(userList);
        assertEquals(1, userList.size());
        verify(userRepository, times(1)).findAll();
    }
}
