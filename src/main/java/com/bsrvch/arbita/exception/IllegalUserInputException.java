package com.bsrvch.arbita.exception;

public class IllegalUserInputException extends RuntimeException {
    public IllegalUserInputException(String errorMessage) {
        super(errorMessage);
    }
}
