package com.trimble.trimblecars.security;

import com.trimble.trimblecars.entity.AuthenticationProvider;
import com.trimble.trimblecars.entity.User;
import com.trimble.trimblecars.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger logger = LogManager.getLogger();

    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException
    {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        // Extract user details
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Check if the user exists
        User user = userService.getUserByEmail(email);

        if (user == null)
        {
            // Redirect the user to role selection or save with default role
            user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setAuthenticationProvider(AuthenticationProvider.GITHUB);
            userService.registerUser(user);
        }

        // Return the authenticated user
        return oAuth2User;
    }
}
