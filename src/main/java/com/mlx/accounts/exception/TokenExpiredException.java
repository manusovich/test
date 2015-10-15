package com.mlx.accounts.exception;

/**
 * 9/25/14.
 */
public class TokenExpiredException extends ApplicationException {
    public TokenExpiredException(String message) {
        super(ApplicationExceptionGroup.TOKEN, message);
    }
}
