package com.mlx.accounts.exception;

/**
 * 9/25/14.
 */
public class NotificationException extends ApplicationException {
    public NotificationException(String message) {
        super(ApplicationExceptionGroup.NOTIFICATION, message);
    }
}
