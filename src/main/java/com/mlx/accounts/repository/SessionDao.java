package com.mlx.accounts.repository;

import com.mlx.accounts.model.entity.SessionEntity;

import java.util.List;

/**
 * 9/24/14.
 */
public interface SessionDao<T extends SessionEntity> extends Dao<T, Long> {
    SessionEntity bySessionKey(String sessionKey);

    List<SessionEntity> byAccountId(Long id);

    void removeUnused();
}
