package com.mlx.accounts.support;

import com.mlx.accounts.model.Token;

import javax.ws.rs.core.NewCookie;
import java.util.Date;

/**
 * 9/7/15.
 */
public class Cookies {
    public static final String COOKIE_SESSION_ID = "T";

    public static NewCookie tokenCookie() {
        return tokenCookie(null, new Date().getTime());
    }

    public static NewCookie tokenCookie(String token, Long expires) {
        return new NewCookie(
                COOKIE_SESSION_ID,
                token,
                "/",  // path
                null, //domain
                1, // version
                null, // comment
                NewCookie.DEFAULT_MAX_AGE,
                new Date(expires), // expires
                false,
                false);
    }

    public static NewCookie tokenCookie(Token token) {
        return tokenCookie(token.getToken(), token.getExpiration().getMillis());
    }
}

