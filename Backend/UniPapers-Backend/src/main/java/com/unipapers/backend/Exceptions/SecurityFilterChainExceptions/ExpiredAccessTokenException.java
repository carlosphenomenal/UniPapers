package com.unipapers.backend.Exceptions.SecurityFilterChainExceptions;

import org.springframework.security.core.AuthenticationException;

public class ExpiredAccessTokenException extends AuthenticationException {

    public ExpiredAccessTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

