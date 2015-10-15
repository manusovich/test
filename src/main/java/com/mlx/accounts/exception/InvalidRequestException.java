package com.mlx.accounts.exception;

import org.eclipse.jetty.http.HttpStatus;

/**
 * 9/25/14.
 */
public class InvalidRequestException extends ApplicationException {
    public InvalidRequestException(String message) {
        super(HttpStatus.BAD_REQUEST_400, ApplicationExceptionGroup.GENERIC, message);
    }
}
