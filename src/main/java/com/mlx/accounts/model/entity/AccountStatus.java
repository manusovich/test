package com.mlx.accounts.model.entity;

/**
 * 9/24/14.
 */
public enum AccountStatus {
    // don't change first 3 items here ! we are using numbers in queries in AccountDaoImpl
    CREATED,
    ON_VERIFICATION,
    VERIFIED,
    BLOCKED
}
