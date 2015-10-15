package com.mlx.accounts.exception;

/**
 * Generic exception for application.
 * Contains code & message which will be returned to user
 * <p>
 * 9/8/14.
 */
public class ApplicationException extends Exception {
    public static final int DEFAULT_HTTP_STATUS_CODE = 500;

    /**
     * Contains the same HTTP Status code returned by the server
     */
    private int status = DEFAULT_HTTP_STATUS_CODE;

    /**
     * Application specific error code
     */
    private ApplicationExceptionGroup code = ApplicationExceptionGroup.GENERIC;


    public ApplicationException() {
        super();
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(ApplicationExceptionGroup code, String message) {
        super(message);
        this.code = code;
    }

    public ApplicationException(int status, ApplicationExceptionGroup code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ApplicationExceptionGroup getCode() {
        return code;
    }

    public void setCode(ApplicationExceptionGroup code) {
        this.code = code;
    }

}
