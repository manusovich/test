package com.mlx.accounts.model;

/**
 * 12/29/14.
 */
public class OAuthSyncResult implements OAuthOperationResult {
    public String forward;
    private boolean accountExistsConflict;

    public OAuthSyncResult() {
    }

    public String getForward() {
        return forward;
    }

    public void setForward(String forward) {
        this.forward = forward;
    }

    public boolean isAccountExistsConflict() {
        return accountExistsConflict;
    }

    public void setAccountExistsConflict(boolean accountExistsConflict) {
        this.accountExistsConflict = accountExistsConflict;
    }
}
