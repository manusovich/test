package com.mlx.accounts.auth;

import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenParser;
import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenVerifier;
import com.github.toastshaman.dropwizard.auth.jwt.exceptions.JsonWebTokenException;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.google.common.base.Optional;
import com.mlx.accounts.support.Cookies;
import io.dropwizard.auth.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

public class JWTAuthFactory<T> extends AuthFactory<JsonWebToken, T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthFactory.class);
    private final boolean required;
    private final Class<T> generatedClass;
    private final String realm;
    private String prefix = "Bearer";
    private UnauthorizedHandler unauthorizedHandler = new DefaultUnauthorizedHandler();
    private final JsonWebTokenVerifier tokenVerifier;
    private final JsonWebTokenParser tokenParser;

    @Context
    private HttpServletRequest request;

    public JWTAuthFactory(Authenticator<JsonWebToken, T> authenticator, String realm,
                          Class<T> generatedClass, JsonWebTokenVerifier tokenVerifier,
                          JsonWebTokenParser tokenParser) {
        super(authenticator);
        this.required = false;
        this.realm = realm;
        this.generatedClass = generatedClass;
        this.tokenParser = tokenParser;
        this.tokenVerifier = tokenVerifier;
    }

    private JWTAuthFactory(boolean required, Authenticator<JsonWebToken, T> authenticator, String realm, Class<T> generatedClass, JsonWebTokenVerifier tokenVerifier, JsonWebTokenParser tokenParser) {
        super(authenticator);
        this.required = required;
        this.realm = realm;
        this.generatedClass = generatedClass;
        this.tokenParser = tokenParser;
        this.tokenVerifier = tokenVerifier;
    }

    public JWTAuthFactory<T> prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public JWTAuthFactory<T> responseBuilder(UnauthorizedHandler unauthorizedHandler) {
        this.unauthorizedHandler = unauthorizedHandler;
        return this;
    }

    public AuthFactory<JsonWebToken, T> clone(boolean required) {
        return (new JWTAuthFactory(required, this.authenticator(),
                this.realm, this.generatedClass, this.tokenVerifier, this.tokenParser))
                .prefix(this.prefix).responseBuilder(this.unauthorizedHandler);
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public T provide() {
        if (this.request != null) {
            String header = this.request.getHeader("Authorization");

            try {
                if (header != null) {
                    int spacePos = header.indexOf(32);
                    if (spacePos > 0) {
                        String method = header.substring(0, spacePos);
                        if (this.prefix.equalsIgnoreCase(method)) {
                            String rawToken = header.substring(spacePos + 1);
                            T result = parseToken(rawToken);
                            if (result != null) return result;
                        }
                    }
                }

                Cookie[] cookies = request.getCookies();
                if (cookies != null && cookies.length > 0) {
                    for (Cookie cookie : cookies) {
                        if (cookie != null && Cookies.COOKIE_SESSION_ID.equalsIgnoreCase(cookie.getName())) {
                            String rawToken = cookie.getValue();
                            T result = parseToken(rawToken);
                            if (result != null) return result;
                            break;
                        }
                    }
                }
            } catch (JsonWebTokenException var7) {
                LOGGER.warn("Error decoding credentials: " + var7.getMessage(), var7);
            } catch (AuthenticationException var8) {
                LOGGER.warn("Error authenticating credentials", var8);
                throw new InternalServerErrorException();
            }
        }

        if (this.required) {
            throw new WebApplicationException(
                    this.unauthorizedHandler.buildResponse(this.prefix, this.realm));
        } else {
            return null;
        }
    }

    private T parseToken(String rawToken) throws AuthenticationException {
        JsonWebToken token = this.tokenParser.parse(rawToken);
        this.tokenVerifier.verifySignature(token);
        Optional<T> result = this.authenticator().authenticate(token);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public Class<T> getGeneratedClass() {
        return this.generatedClass;
    }
}