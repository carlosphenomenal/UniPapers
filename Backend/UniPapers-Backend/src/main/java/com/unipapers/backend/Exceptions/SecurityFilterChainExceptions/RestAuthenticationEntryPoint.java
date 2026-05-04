package com.unipapers.backend.Exceptions.SecurityFilterChainExceptions;

import com.unipapers.backend.Exceptions.Models.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String EXPIRED_TOKEN_HEADER = "X-Auth-Error";
    private static final String EXPIRED_TOKEN_VALUE = "token_expired";

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException authException) throws IOException {

        boolean isExpired = authException instanceof ExpiredAccessTokenException;
        String message = isExpired ? "Access token expired" : "Authentication required";

        if (isExpired) {
            response.setHeader(EXPIRED_TOKEN_HEADER, EXPIRED_TOKEN_VALUE);
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer error=\"invalid_token\", error_description=\"access token expired\"");
        }

        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "UNAUTHORIZED",
                message,
                request.getRequestURI(),
                UUID.randomUUID().toString()
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
