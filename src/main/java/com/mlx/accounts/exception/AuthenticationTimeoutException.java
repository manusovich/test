package com.mlx.accounts.exception;

/**
 * 9/25/14.
 */
public class AuthenticationTimeoutException extends ApplicationException {

    private static final int TIKEN_EXPIRED = 419; // not part of rfc

    public AuthenticationTimeoutException(String message) {
        super(TIKEN_EXPIRED, ApplicationExceptionGroup.TOKEN, message);
    }
}
