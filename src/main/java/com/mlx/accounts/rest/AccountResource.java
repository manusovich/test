package com.mlx.accounts.rest;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.mlx.accounts.AppConfiguration;
import com.mlx.accounts.exception.ApplicationException;
import com.mlx.accounts.model.Account;
import com.mlx.accounts.model.ActivationAccount;
import com.mlx.accounts.model.NewPassword;
import com.mlx.accounts.model.Token;
import com.mlx.accounts.model.entity.AccountEntity;
import com.mlx.accounts.model.entity.ActivationResult;
import com.mlx.accounts.service.AccountService;
import com.mlx.accounts.support.Cookies;
import com.mlx.accounts.support.JWT;
import io.dropwizard.auth.Auth;

import javax.inject.Singleton;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 9/9/14.
 */
@Singleton
@Path("/api/account")
public class AccountResource {
    @Inject
    private JWT jwt;
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
    public Response create(@Valid Account account) throws ApplicationException {
        accountService.create(newAccountEntity(account), true);
        return Response.status(Response.Status.CREATED)
                .build();
    }

    private AccountEntity newAccountEntity(Account account) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setUid(UUID.randomUUID().toString());
        accountEntity.setEmail(account.getEmail());
        accountEntity.setUserName(account.getUserName());
        return accountEntity;
    }

    /*
      * Read
      */
    @GET
    @Timed
    @Path("/all/")
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(
            @Auth Account account,
            @Context UriInfo uriInfo)
            throws ApplicationException {
        account.assertAdmin();

        Long count, page;
        Map<String, String> sort = new HashMap<>();

        try {
            String countStr = uriInfo.getQueryParameters().getFirst("count");
            String pageStr = uriInfo.getQueryParameters().getFirst("page");
            if (countStr == null || countStr.isEmpty()) {
                count = 10l;
            } else {
                count = Long.valueOf(countStr);
                if (count < 1 || count > 20) {
                    throw new ApplicationException("Count should be > 0 & <= 20");
                }
            }
            if (pageStr == null || pageStr.isEmpty()) {
                page = 1l;
            } else {
                page = Long.valueOf(pageStr);
                if (page < 1) {
                    throw new ApplicationException("Page should be > 0");
                }
            }


            for (String key : uriInfo.getQueryParameters().keySet()) {
                String sorting = "sorting";
                if (key.indexOf(sorting) == 0) {
                    sort.put(key.substring(sorting.length() + 1, key.length() - 1),
                            uriInfo.getQueryParameters().getFirst(key));
                }
            }
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }

        return Response.status(Response.Status.OK)
                .entity(accountService.getAccounts(count, page, sort))
                .build();
    }

    /*
     * Read
     */
    @GET
    @Timed
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(@Auth Account account) {
        return Response.status(Response.Status.OK)
                .entity(account)
                .build();
    }

    /*
     * Read specific profile
     */
    @GET
    @Timed
    @Path("/{uid}/")
    @Produces({MediaType.APPLICATION_JSON})
    public Response get(@Auth(required = false) Account caller, @PathParam("uid") String uid) {
        return Response.status(Response.Status.OK)
                .entity(accountService.readExtracted(caller, uid, false))
                .build();
    }

    @GET
    @Path("/picture/")
    public Response pictureNull(@Auth Account caller,
                                @PathParam("uid") String uid,
                                @Context HttpServletResponse response) throws IOException {
        return picture(caller, "", response);
    }

    @GET
    @Path("/picture/{uid}")
    public Response picture(@Auth Account caller,
                            @PathParam("uid") String uid,
                            @Context HttpServletResponse response) throws IOException {
        String url = accountService.readUserPicture(caller, uid);

        InputStream input;
        if (url != null) {
            URL oracle = new URL(url);
            input = oracle.openConnection().getInputStream();
        } else {
            input = getClass().getResourceAsStream("/default-user.png");
        }

        byte[] buffer = new byte[4096];
        int n;
        ServletOutputStream outStream = response.getOutputStream();
        while ((n = input.read(buffer)) != -1) {
            if (n > 0) {
                outStream.write(buffer, 0, n);
            }
        }
        outStream.flush();
        outStream.close();
        input.close();
        return Response.ok().build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/activate")
    public Response activate(
            @Valid ActivationAccount activation,
            @Context HttpServletRequest requestContext) throws ApplicationException {

        ActivationResult activationResult = accountService.activate(activation);
        Token token = jwt.generate(activationResult.getAccount().getUid(),
                requestContext.getRemoteAddr());

        return Response
                .ok()
                .cookie(Cookies.tokenCookie(token))
                .build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/forgotPassword")
    public Response forgotPassword(String email) throws ApplicationException {
        try {
            accountService.forgotPassword(email);
        } catch (Exception ex) {
            // we don't want to show if such mail is exist
        }
        return Response
                .status(Response.Status.OK)
                .build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/newPassword")
    public Response newPassword(@Valid NewPassword newPassword) throws ApplicationException {
        accountService.newPassword(newPassword.getCode(), newPassword.getPassword());
        return Response
                .status(Response.Status.OK)
                .build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response update(
            @Auth Account caller,
            @Valid Account account) throws ApplicationException {

        accountService.update(caller.getUid(), account);

        return Response
                .status(Response.Status.OK)
                .entity(accountService.readExtracted(caller))
                .build();
    }

    /*
    * Delete
    */
    @DELETE
    public Response delete(@Auth Account account) throws ApplicationException {
        accountService.removeSelf(account);
        return Response
                .noContent()
                .build();
    }

    @DELETE
    @Path("/deleteAll")
    public Response deleteAll(
            @Auth Account caller)
            throws ApplicationException {
        caller.assertAdmin();

        accountService.removeAllAccounts(caller);
        return Response.status(Response.Status.NO_CONTENT)
                .build();
    }

    @DELETE
    @Path("/{uid}/")
    public Response deleteAccount(
            @Auth Account caller,
            @PathParam("uid") String uid)
            throws ApplicationException {
        caller.assertAdmin();

        accountService.remove(uid);
        return Response.status(Response.Status.NO_CONTENT)
                .build();
    }
}
