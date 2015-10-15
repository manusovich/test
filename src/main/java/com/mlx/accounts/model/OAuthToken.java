package com.mlx.accounts.model;

/**
 * 12/7/14.
 */
public class OAuthToken {
    private String token;
    private Long lifetime;

    public OAuthToken() {
    }

    public OAuthToken(String token, Long lifetime) {
        this.token = token;
        this.lifetime = lifetime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getLifetime() {
        return lifetime;
    }

    public void setLifetime(Long lifetime) {
        this.lifetime = lifetime;
    }
}
