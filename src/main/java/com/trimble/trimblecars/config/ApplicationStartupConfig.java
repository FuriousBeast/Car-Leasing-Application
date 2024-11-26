package com.trimble.trimblecars.config;

import com.trimble.trimblecars.entity.Role;
import com.trimble.trimblecars.entity.User;
import com.trimble.trimblecars.service.AuthService;
import com.trimble.trimblecars.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * ApplicationStartupConfig.java
 *
 * @author Nandhakumar N (nandhakumarn@nmsworks.co.in)
 * @module com.trimble.trimblecars.config
 * @created Nov 26, 2024
 */
@Configuration
public class ApplicationStartupConfig
{
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @EventListener(ApplicationReadyEvent.class)
    public void init()
    {
        User user = new User();
        user.setName("Admin");
        user.setRole(Role.ADMIN);
        user.setEmail("admin@email.com");
        user.setName("superadmin");
        user.setPassword("superadmin");

        userService.registerUser(user);

        String jwtToken = authService.generateToken(user);

        logger.info("Admin Jwt Token {}", jwtToken);
    }
}
