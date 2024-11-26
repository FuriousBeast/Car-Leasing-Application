package com.trimble.trimblecars.service;

import com.trimble.trimblecars.entity.User;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * TokenService.java
 *
 * @author Nandhakumar N (nandhakumarn@nmsworks.co.in)
 * @module com.trimble.trimblecars.service
 * @created Nov 25, 2024
 */

@Service
@RequiredArgsConstructor
public class TokenService
{
    private final AuthService authService;

    @Value("${spring.security.oauth2.client.provider.github.user-info-uri}")
    private String githubBaseUrl;

    @Value("${spring.security.oauth2.client.provider.google.token-validation-uri}")
    private String googleBaseUrl;

    private static final Logger logger = LogManager.getLogger();

    private final UserService userService;

    private static final RestTemplate restTemplate = new RestTemplate();

    static HttpHeaders httpHeaders = new HttpHeaders();

    static HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

    @SuppressWarnings({"unchecked","rawtypes"})
    public User validateTokenAndRetrieveUser(String token) throws Exception
    {
        String email;

        if(token.startsWith("gho_")) // Github
        {
            logger.info("Github authorization");

            httpHeaders.set("Authorization", "Bearer "+token);
            ResponseEntity<Map> exchange = restTemplate.exchange(githubBaseUrl, HttpMethod.GET, entity, Map.class);

            Map<String, Object> response = exchange.getBody();

            if (response == null || response.containsKey("error")) {
                return null; // Token is invalid or expired
            }

            email = (String) response.get("email");
        }
        else if(token.startsWith("ey")) // Local user
        {
            logger.info("Local authorization");

            email = authService.extractUsername(token);
        }
        else // assuming it as google
        {
            logger.info("Google authorization");

            String authorizationUrl = googleBaseUrl + token;
            // Call Google's introspection endpoint
            Map<String, Object> response = restTemplate.getForObject(authorizationUrl, Map.class);

            if(response == null || response.containsKey("error"))
                return null;

            email = (String) response.get("email");
        }

        return userService.getUserByEmail(email);
    }
}
