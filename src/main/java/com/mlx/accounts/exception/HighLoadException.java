package com.mlx.accounts.exception;

import org.eclipse.jetty.http.HttpStatus;

/**
 * 9/25/14.
 */
public class HighLoadException extends ApplicationException {
    public HighLoadException(String message) {
        super(HttpStatus.SERVICE_UNAVAILABLE_503, ApplicationExceptionGroup.HIGHLOAD, message);
    }
}
