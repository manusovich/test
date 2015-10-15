package com.mlx.accounts.exception;


import org.eclipse.jetty.http.HttpStatus;

/**
 * 9/25/14.
 */
public class AccountAlreadyExistsException extends ApplicationException {
    public AccountAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT_409, ApplicationExceptionGroup.GENERIC, message);
    }
}
