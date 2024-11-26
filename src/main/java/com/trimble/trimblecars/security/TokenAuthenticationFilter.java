package com.trimble.trimblecars.security;

import com.trimble.trimblecars.dto.ApiResponse;
import com.trimble.trimblecars.dto.ResponseType;
import com.trimble.trimblecars.entity.Role;
import com.trimble.trimblecars.entity.User;
import com.trimble.trimblecars.service.TokenService;
import com.trimble.trimblecars.utils.CommonUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * TokenAuthenticationFilter.java
 *
 * @author Nandhakumar N (nandhakumarn@nmsworks.co.in)
 * @module com.trimble.trimblecars.security
 * @created Nov 25, 2024
 */

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter
{
    private final TokenService tokenService;

    private static final Logger logger = LogManager.getLogger();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");

        try
        {
            if (authHeader != null && authHeader.startsWith("Bearer "))
            {
                String token = authHeader.substring(7);

                //Validate the token
                User userDetails = tokenService.validateTokenAndRetrieveUser(token);

                if (userDetails != null)
                {
                    //Save the authentication in Security Context
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()));

                    if(userDetails.getRole().equals(Role.ROLE_PENDING)  && !request.getServletPath().contains("/auth/updateRole"))
                    {
                        sendJsonErrorResponse(response, "Your account role is currently pending. To proceed, please update your role by sending a PUT request to /user/update with the desired role (e.g., 'car_owner', 'customer', etc.).");
                        return;
                    }
                }
                else
                {
                    //returns response that user found in token is not registered
                    sendJsonErrorResponse(response, "User not found in records!");
                    return;
                }

            }
            else
            {
                //Authorization header is missing
                sendJsonErrorResponse(response, "Authorization required!");
                return;
            }

            //continue to filtering request
            filterChain.doFilter(request, response);
        }
        catch (Exception exception)
        {
            logger.error(exception);

            //token not valid
            sendJsonErrorResponse(response, "Invalid token");
        }
    }

    private void sendJsonErrorResponse(HttpServletResponse response, String message) throws IOException
    {
        if(!response.isCommitted())
            response.reset();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Construct the JSON error response
        ApiResponse<String> apiResponse = ApiResponse.<String>builder().responseType(ResponseType.ERROR).responseMessage(message).build();

        response.getWriter().write(Objects.requireNonNull(CommonUtils.convertToJson(apiResponse)));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException
    {
        String path = request.getServletPath();
        return path.startsWith("/static/") || path.contains("css") || path.contains("js") || path.equals("/favicon") ||  path.contains("login");
    }
}




