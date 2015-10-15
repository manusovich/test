package com.mlx.accounts.support;

import com.mlx.accounts.model.entity.AccountRole;
import com.mlx.accounts.model.entity.SessionEntity;

/**
 * 5/22/15.
 */
public final class SessionUtils {
    private SessionUtils() {

    }

    public static boolean isValid(SessionEntity sessionEntity) {
        return sessionEntity != null
                && sessionEntity.getAccount() != null
                && sessionEntity.getCreated() + sessionEntity.getLifeTime() > System.currentTimeMillis();
    }

    public static boolean isValid(SessionEntity sessionEntity, AccountRole role) {
        return sessionEntity != null
                && sessionEntity.getAccount() != null
                && sessionEntity.getCreated() + sessionEntity.getLifeTime() > System.currentTimeMillis()
                && role.toString().equals(sessionEntity.getAccount().getRole());
    }
}
