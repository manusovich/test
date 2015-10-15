package com.mlx.accounts.exception;


import org.eclipse.jetty.http.HttpStatus;

/**
 * 9/25/14.
 */
public class NotEnoughPointsException extends ApplicationException {
    public NotEnoughPointsException(String message) {
        super(HttpStatus.BAD_REQUEST_400, ApplicationExceptionGroup.NOT_ENOUGH_POINTS, message);
    }
}
