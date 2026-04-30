package com.unipapers.backend.Configurations.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable) // disable CSRF
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // allow everything for now, we'll implement spring security later
                )
                .build();
    }

    /**
     * Creates and configures a {@link PasswordEncoder} bean using {@link BCryptPasswordEncoder}.
     * This PasswordEncoder is used to hash and validate passwords securely within the application.
     *
     * @return a {@link BCryptPasswordEncoder} instance with strength set to 12, ensuring strong password hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }


    /**
     * Provides an {@link AuthenticationManager} bean for managing authentication logic within the application.
     * This method retrieves the {@link AuthenticationManager} instance from the given {@link AuthenticationConfiguration}.
     *
     * @param configuration the {@link AuthenticationConfiguration} that provides the {@link AuthenticationManager}.
     * @return the {@link AuthenticationManager} instance configured in the application.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

}
