package com.unipapers.backend.Exceptions.CustomExceptions;

public class PastPaperAlreadyExistsByHashException extends RuntimeException {
    public PastPaperAlreadyExistsByHashException(String message) {
        super(message);
    }
}
