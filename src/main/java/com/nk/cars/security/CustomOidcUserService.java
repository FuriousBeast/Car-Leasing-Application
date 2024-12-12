package com.nk.cars.security;

import com.nk.cars.entity.AuthenticationProvider;
import com.nk.cars.entity.Role;
import com.nk.cars.entity.User;
import com.nk.cars.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * CustomOidcUserService.java
 *
 * @author Nandhakumar N (nandhuhandsome2000@gmail.com)
 * @module com.nk.cars.security
 * @created Nov 26, 2024
 */

@Component
public class CustomOidcUserService extends OidcUserService
{
    public static final Logger logger = LogManager.getLogger();

    @Autowired
    private UserService userService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException
    {
        OidcUser oidcUser = super.loadUser(userRequest);

        Map<String, Object> attributes = oidcUser.getAttributes();

        // Example: Extract specific details
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        User user = userService.getUserByEmail(email);

        if (user == null) {
            // Redirect the user to role selection or save with default role
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setAuthenticationProvider(AuthenticationProvider.GOOGLE);
            user.setRole(Role.ROLE_PENDING); // Default role until selection
            userService.registerUser(user);
        }

        // Return the user or customize the returned user object
        return oidcUser;
    }
}
