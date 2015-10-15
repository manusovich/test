package com.mlx.accounts.exception;

import org.eclipse.jetty.http.HttpStatus;

/**
 * 9/25/14.
 */
public class OAuthAccountAlreadyUsedException extends ApplicationException {

    public OAuthAccountAlreadyUsedException(String message) {
        super(HttpStatus.CONFLICT_409, ApplicationExceptionGroup.LINKEDIN, message);
    }

}
