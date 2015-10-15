package com.mlx.accounts.model;

import org.joda.time.DateTime;

/**
 * 12/29/14.
 */
public class Token {
    private String token;
    private DateTime expiration;

    public Token() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public DateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(DateTime expiration) {
        this.expiration = expiration;
    }
}
