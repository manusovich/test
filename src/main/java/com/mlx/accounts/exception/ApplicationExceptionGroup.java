package com.mlx.accounts.exception;

/**
 * Enum with application exception groups.
 * Value will be returned to the client
 * <p>
 * 9/10/14.
 */
public enum ApplicationExceptionGroup {
    GENERIC,
    ACCOUNT,
    TOKEN,
    LINKEDIN,
    NOTIFICATION,
    NOT_ENOUGH_POINTS,
    TOO_MANY_REQUESTS,
    ACCESS,
    HIGHLOAD
}
