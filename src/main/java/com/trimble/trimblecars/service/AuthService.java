package com.trimble.trimblecars.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.trimble.trimblecars.entity.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AuthService {

    private static final String SECRET_KEY = "trimble";

    public String generateToken(User user) {
        return JWT.create()
                .withSubject(user.getEmail()) // Use username or ID as subject
                .withClaim("roles", user.getAuthorities().toString()) // Add roles
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public String extractUsername(String token) throws JWTVerificationException
    {
        return JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(token)
                .getSubject();
    }

    public User fetchUserFromAuth()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User principal = (User) authentication.getPrincipal();

        if(principal == null)
            throw new AccessDeniedException("Login again");

        return principal;
    }

}
