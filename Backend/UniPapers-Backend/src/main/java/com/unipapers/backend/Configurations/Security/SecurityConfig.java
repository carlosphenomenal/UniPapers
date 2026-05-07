package com.unipapers.backend.Configurations.Security;

import com.unipapers.backend.Exceptions.SecurityFilterChainExceptions.RestAccessDeniedHandler;
import com.unipapers.backend.Exceptions.SecurityFilterChainExceptions.RestAuthenticationEntryPoint;
import com.unipapers.backend.Modules.Auth.Services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   AuthenticationProvider authenticationProvider,
                                                   RestAccessDeniedHandler accessDeniedHandler,
                                                   RestAuthenticationEntryPoint authenticationEntryPoint) {
        return http
                .csrf(AbstractHttpConfigurer::disable) // disable CSRF
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/me").authenticated()
                        .requestMatchers("/auth/**", "/programs/get", "/notifications/**", "/auth/update-fcm-token").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        // Handle 401
                        .authenticationEntryPoint(authenticationEntryPoint)
                        // Handle 403
                        .accessDeniedHandler(accessDeniedHandler)
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
     * Configures and provides an {@link AuthenticationProvider} bean.
     * This method sets up a {@link DaoAuthenticationProvider}, integrates the application's
     * {@link CustomUserDetailsService}, and configures the provided password encoder for authenticating users.
     *
     * @param passwordEncoder the {@link PasswordEncoder} to be used for encoding and verifying passwords.
     * @return an {@link AuthenticationProvider} configured with the application's {@link CustomUserDetailsService}
     *         and the provided {@link PasswordEncoder}.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder, CustomUserDetailsService customUserDetailsService){

        DaoAuthenticationProvider provider=new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;

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
