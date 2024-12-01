package com.trimble.trimblecars.security;

import com.trimble.trimblecars.entity.AuthenticationProvider;
import com.trimble.trimblecars.entity.Role;
import com.trimble.trimblecars.entity.User;
import com.trimble.trimblecars.service.UserService;
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
 * @author Nandhakumar N (nandhakumarn@nmsworks.co.in)
 * @module com.trimble.trimblecars.security
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
