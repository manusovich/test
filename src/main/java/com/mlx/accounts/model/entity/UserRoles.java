package com.mlx.accounts.model.entity;

/**
 * User roles
 * <p>
 * 9/9/14.
 */
public interface UserRoles {
    /**
     * Role for case when we know about user's session. Some API requires this role
     */
    public final static String AUTHORIZED = "AUTHORIZED";
    public final static String ADMINISTRATOR = "ADMINISTRATOR";
}

