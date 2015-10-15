package com.mlx.accounts.rest;

import com.google.inject.Inject;
import com.mlx.accounts.AppConfiguration;
import com.mlx.accounts.exception.ApplicationException;
import com.mlx.accounts.model.Account;
import com.mlx.accounts.model.Credentials;
import com.mlx.accounts.model.Token;
import com.mlx.accounts.model.entity.UserRoles;
import com.mlx.accounts.service.AccountService;
import com.mlx.accounts.support.Cookies;
import io.dropwizard.auth.Auth;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collections;

/**
 * 9/9/14.
 */
@Singleton
@Path("/api/session")
@PermitAll
public class SessionResource {
    @Inject
    private AccountService accountService;

    @Inject
    private AppConfiguration appConfiguration;

    /*
     * Create
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response create(
            @Context HttpServletRequest requestContext,
            @Valid Credentials credentials) throws ApplicationException {

        String ip = requestContext.getRemoteAddr();
        Token token = getAccountService().signOn(
                credentials.getEmail(), credentials.getPassword(), ip);

        return Response.status(Response.Status.CREATED)
                .cookie(Cookies.tokenCookie(token))
                .build();
    }

    /*
     * Read
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed(UserRoles.AUTHORIZED)
    public Response get(
            @Auth Account account) {
        return Response.status(Response.Status.OK)
                .entity(Collections.singletonMap("account", account))
                .build();
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @RolesAllowed(UserRoles.AUTHORIZED)
    @Path("/logout")
    public Response deleteSession(
            @Auth Account account) {
        return deleteSession();
    }

    /*
     * Delete
     */
    @DELETE
    @Produces({MediaType.TEXT_HTML})
    @RolesAllowed(UserRoles.AUTHORIZED)
    public Response deleteSession() {
        return Response
                .seeOther(URI.create(appConfiguration.getUrl()))
                .cookie(Cookies.tokenCookie())
                .build();
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
}
