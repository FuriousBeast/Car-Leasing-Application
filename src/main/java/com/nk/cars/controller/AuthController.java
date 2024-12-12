package com.nk.cars.controller;

import com.nk.cars.dto.ApiResponse;
import com.nk.cars.dto.ResponseType;
import com.nk.cars.entity.User;
import com.nk.cars.service.AuthService;
import com.nk.cars.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private OAuth2AuthorizedClientService clientService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public ApiResponse<String> authenticateUser(OAuth2AuthenticationToken authentication)
    {
        if(authentication == null)
            return ApiResponse.<String>builder()
                    .responseType(ResponseType.ERROR)
                    .responseMessage("Authorization Required")
                    .build();

        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());

        String accessToken = client.getAccessToken().getTokenValue();

        logger.info("Access Token formed {}", accessToken);

        return ApiResponse.<String>builder()
                .responseType(ResponseType.SUCCESS)
                .responseMessage("Authorization Success")
                .returnResponse("AccessToken: " + accessToken)
                .build();
    }

    @PutMapping("/updateRole")
    public ResponseEntity<ApiResponse<User>> updateUser(@RequestParam String role)
    {
        User userFromAuth = authService.fetchUserFromAuth();

        logger.info("Received request to update user with username: {}", userFromAuth.getUsername());

        User updatedUser = userService.updateUser(userFromAuth.getEmail(), role);

        ApiResponse<User> apiResponse = ApiResponse.<User>builder().returnResponse(updatedUser).responseType(ResponseType.SUCCESS).responseMessage("User updated successfully!").build();

        logger.info("User update successfully with email Id : {}", updatedUser.getEmail());

        return ResponseEntity.ok(apiResponse);
    }
}
