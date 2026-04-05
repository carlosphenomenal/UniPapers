package com.unipapers.backend.Exceptions.CustomExceptions;

public class PastPaperNotFoundException extends RuntimeException {
    public PastPaperNotFoundException(String message) {
        super(message);
    }
}

