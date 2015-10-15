package com.mlx.accounts.exception;

import org.eclipse.jetty.http.HttpStatus;

/**
 * 9/25/14.
 */
public class UnauthorizedException extends ApplicationException {
    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED_401, ApplicationExceptionGroup.ACCESS, message);
    }
}
