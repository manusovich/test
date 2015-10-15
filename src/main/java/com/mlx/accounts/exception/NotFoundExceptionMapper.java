package com.mlx.accounts.exception;

import com.mlx.accounts.AppConfiguration;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.net.URI;

/**
 * Exception mapper for 404 errors.
 * <p>
 * 9/8/14.
 */
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Inject
    private AppConfiguration appConfiguration;

    public Response toResponse(NotFoundException ex) {

        try {
//            RollbarNotifier.notify(ex);
        } catch (Throwable throwable) {
            ex.printStackTrace();
        }

        return Response
                .seeOther(URI.create(appConfiguration.getUrl() + "/error/404"))
                .build();
    }

}
