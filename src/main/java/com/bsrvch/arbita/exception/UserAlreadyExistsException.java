package com.bsrvch.arbita.exception;

public class UserAlreadyExistsException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "User with telegram id \"%s\" already exists.";

    public UserAlreadyExistsException(String userTelegramId) {
        super(String.format(MESSAGE_TEMPLATE, userTelegramId));
    }
}
