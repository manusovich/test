package com.mlx.accounts.exception;


import com.mlx.accounts.model.ApplicationError;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for app errors which can be thrown from application
 * <p>
 * 9/8/14.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    public static final String MESSAGE = "Can't process your request";

    public Response toResponse(Throwable ex) {
        ApplicationError errorMessage = new ApplicationError();
        setHttpStatus(ex, errorMessage);

        if (!(ex instanceof ApplicationException)) {
            errorMessage.setMessage(MESSAGE);
        } else {
            if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
                errorMessage.setMessage(ex.getMessage());
            } else {
                errorMessage.setMessage(MESSAGE);
            }
        }

        if (!(ex instanceof ForbiddenException)) {
            try {
//                RollbarNotifier.notify(ex);
            } catch (Throwable throwable) {
                ex.printStackTrace();
            }
            ex.printStackTrace();
        }


        return Response.status(errorMessage.getStatus())
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private void setHttpStatus(Throwable ex, ApplicationError errorMessage) {
        if (ex instanceof WebApplicationException) {
            errorMessage.setStatus(((WebApplicationException) ex).getResponse().getStatus());
        } else {
            errorMessage.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }
}

