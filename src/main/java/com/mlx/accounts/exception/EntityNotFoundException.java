package com.mlx.accounts.exception;

import org.eclipse.jetty.http.HttpStatus;

/**
 * 9/25/14.
 */
public class EntityNotFoundException extends ApplicationException {
    public EntityNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND_404, ApplicationExceptionGroup.ACCOUNT, message);
    }
}
