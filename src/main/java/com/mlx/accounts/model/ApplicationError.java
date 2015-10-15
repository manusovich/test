package com.mlx.accounts.model;

import com.mlx.accounts.exception.ApplicationException;
import com.mlx.accounts.exception.ApplicationExceptionGroup;
import jodd.bean.BeanCopy;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Bean for application error which will be returned to the client
 * <p>
 * 9/8/14.
 */
@XmlRootElement
public class ApplicationError {

    /**
     * contains the same HTTP Status code returned by the server
     */
    @XmlElement(name = "status", required = false)
    int status;

    /**
     * application specific error code
     */
    @XmlElement(name = "code", required = false)
    ApplicationExceptionGroup code;

    /**
     * message describing the error
     */
    @XmlElement(name = "message", required = false)
    String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ApplicationError(ApplicationException ex) {
        BeanCopy.beans(ex, this).copy();
    }

    public ApplicationError(NotFoundException ex) {
        this.status = Response.Status.NOT_FOUND.getStatusCode();
        this.message = ex.getMessage();
    }

    public ApplicationError() {
    }
}
