package com.mlx.accounts.exception;

import org.eclipse.jetty.http.HttpStatus;

/**
 * 9/25/14.
 */
public class TooManyRequestsException extends ApplicationException {
    public TooManyRequestsException(String message) {
        super(HttpStatus.BAD_REQUEST_400, ApplicationExceptionGroup.TOO_MANY_REQUESTS, message);
    }
}
