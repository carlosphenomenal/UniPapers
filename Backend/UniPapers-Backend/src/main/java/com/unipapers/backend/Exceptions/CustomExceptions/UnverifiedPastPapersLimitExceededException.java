package com.unipapers.backend.Exceptions.CustomExceptions;

public class UnverifiedPastPapersLimitExceededException extends RuntimeException {
    public UnverifiedPastPapersLimitExceededException(String message) {
        super(message);
    }
}
