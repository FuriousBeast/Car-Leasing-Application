package com.nk.cars.security;

import com.nk.cars.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig
{
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private CustomOidcUserService customOidcUserService;

    @Autowired
    private TokenService tokenService;

    @Bean
    @ConditionalOnProperty(value = "spring.application.security", havingValue = "false") //disable security
    public DefaultSecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) throws Exception
    {
        return httpSecurity
                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest()
                        .permitAll())
                .build();
    }

    @Bean
    @ConditionalOnProperty(value = "spring.application.security", havingValue = "true")  //enable security
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        return http
                .csrf(CsrfConfigurer::disable) // disable csrf validation
                .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/auth/**", "/login").permitAll() // makes login page accessible
                    .anyRequest().authenticated())

                .addFilterBefore(new TokenAuthenticationFilter(tokenService), UsernamePasswordAuthenticationFilter.class) // authenticates via token

                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(user -> user
                                .userService(customOAuth2UserService) //handles oauth(github) login
                                .oidcUserService(customOidcUserService)) //handles oidc(google) login
                        .defaultSuccessUrl("/auth/login", true)) // Redirect here after login
                .build();
    }

}
