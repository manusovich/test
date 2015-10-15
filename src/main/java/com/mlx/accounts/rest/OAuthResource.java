package com.mlx.accounts.rest;

import com.google.inject.Inject;
import com.mlx.accounts.AppConfiguration;
import com.mlx.accounts.exception.ApplicationException;
import com.mlx.accounts.model.Account;
import com.mlx.accounts.model.OAuthOperationResult;
import com.mlx.accounts.model.OAuthSession;
import com.mlx.accounts.model.OAuthType;
import com.mlx.accounts.model.entity.UserRoles;
import com.mlx.accounts.repository.ApplicationRepository;
import com.mlx.accounts.service.OAuthService;
import com.mlx.accounts.support.Cookies;
import io.dropwizard.auth.Auth;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * 9/9/14.
 */
@Singleton
@Path("/api/")
@PermitAll
public class OAuthResource {
    private static final Logger logger = LoggerFactory.getLogger(OAuthResource.class);

    @Inject
    private OAuthService oAuthService;

    @Inject
    private ApplicationRepository repository;

    @Inject
    private AppConfiguration appConfiguration;

    @GET
    @Path("/oauth")
    @Produces({MediaType.TEXT_HTML})
    public Response handleToken(
            @QueryParam("code")
            @Length(min = 1, message = "Invalid code")
            String oAuthCode,
            @QueryParam("state")
            @Length(min = 1, message = "Invalid state")
            String verificationToken) throws ApplicationException {
        return callBack(oAuthCode, verificationToken);
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/oauth/authorization")
    public Response authorization(
            @Context HttpServletRequest requestContext,
            @Auth(required = false) Account account,
            @QueryParam("provider") String provider,
            @QueryParam("fwdurl") String forwardURL)
            throws ApplicationException {

        String authURL = getOAuthService().authorizationUrl(
                OAuthType.valueOf(provider), true, account,
                forwardURL, requestContext.getRemoteAddr());

        return Response
                .seeOther(URI.create(authURL))
                .build();
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/oauth/synchronization")
    @RolesAllowed(UserRoles.AUTHORIZED)
    public Response synchronization(
            @Context HttpServletRequest requestContext,
            @Auth Account caller,
            @QueryParam("provider") String provider,
            @QueryParam("fwdurl") String forwardURL)
            throws ApplicationException {

        String authURL = getOAuthService().authorizationUrl(
                OAuthType.valueOf(provider), false, caller, forwardURL,
                requestContext.getRemoteAddr());

        return Response
                .seeOther(URI.create(authURL))
                .build();
    }

    @PUT
    @Produces({MediaType.TEXT_HTML})
    @Path("/oauth/update")
    @RolesAllowed(UserRoles.AUTHORIZED)
    public Response update(
            @Auth Account caller,
            @QueryParam("provider") String provider)
            throws ApplicationException {
        getOAuthService().syncOAuthDataInProfile(caller, OAuthType.valueOf(provider));
        return Response.status(Response.Status.OK)
                .build();
    }

    private Response callBack(String oAuthCode, String verificationToken) {
        String forward = appConfiguration.getOauthConfiguration().getSuccess();
        OAuthOperationResult result = null;

        if (oAuthCode != null && !oAuthCode.isEmpty()) {
            try {
                result = getOAuthService().processOAuth(verificationToken, oAuthCode);
                forward = getOAuthService().oAuthTokenResponseURL(result);
            } catch (Exception e) {
                e.printStackTrace();
                return Response
                        .seeOther(URI.create(appConfiguration.getOauthConfiguration().getIssue()))
                        .build();
            }
        } else {
            logger.error("OAuth code is not available");
        }

        if (result != null && result instanceof OAuthSession) {
            OAuthSession session = new OAuthSession();
            session.setToken(((OAuthSession) result).getToken());
            if (((OAuthSession) result).isNewAccount()) {
                return Response
                        .seeOther(URI.create(appConfiguration.getOauthConfiguration().getSetup()))
                        .cookie(Cookies.tokenCookie(((OAuthSession) result).getToken()))
                        .build();
            } else {
                session.setForward(forward);
            }
            return Response
                    .seeOther(URI.create(forward))
                    .cookie(Cookies.tokenCookie(((OAuthSession) result).getToken()))
                    .build();
        } else {
            return Response
                    .seeOther(URI.create(forward))
                    .build();
        }
    }

    public ApplicationRepository getRepository() {
        return repository;
    }

    public void setRepository(ApplicationRepository repository) {
        this.repository = repository;
    }

    public OAuthService getOAuthService() {
        return oAuthService;
    }

    public void setOAuthService(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }
}
