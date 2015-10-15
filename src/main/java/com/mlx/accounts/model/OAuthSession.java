package com.mlx.accounts.model;

/**
 * 12/29/14.
 */
public class OAuthSession implements OAuthOperationResult {
    private Token token;
    private String forward;
    private boolean newAccount;

    public OAuthSession() {
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String getForward() {
        return forward;
    }

    public void setForward(String forward) {
        this.forward = forward;
    }

    public boolean isNewAccount() {
        return newAccount;
    }

    public void setNewAccount(boolean newAccount) {
        this.newAccount = newAccount;
    }
}
