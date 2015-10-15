package com.mlx.accounts.exception;

/**
 * 9/25/14.
 */
public class InvalidEntityStateException extends ApplicationException {
    public InvalidEntityStateException(String message) {
        super(ApplicationExceptionGroup.GENERIC, message);
    }
}
