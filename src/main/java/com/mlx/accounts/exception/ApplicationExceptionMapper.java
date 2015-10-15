package com.mlx.accounts.exception;


import com.mlx.accounts.model.ApplicationError;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for application errors.
 * Creates wrapper for exception and returns back to client
 * <p>
 * 9/8/14.
 */
@Provider
public class ApplicationExceptionMapper implements ExceptionMapper<ApplicationException> {

    public Response toResponse(ApplicationException ex) {
        if (!(ex instanceof UnauthorizedException) &&
                !(ex instanceof HighLoadException)) {
            ex.printStackTrace();
        }

        if (!(ex instanceof UnauthorizedException)) {
            try {
//                RollbarNotifier.notify(ex);
            } catch (Throwable throwable) {
                ex.printStackTrace();
            }
        }


        ApplicationError applicationError = null;
        applicationError = new ApplicationError(ex);

        return Response.status(ex.getStatus())
                .entity(applicationError)
                .type(MediaType.APPLICATION_JSON).
                        build();
    }

}
