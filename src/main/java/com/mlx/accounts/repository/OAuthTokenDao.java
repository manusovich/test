package com.mlx.accounts.repository;

import com.mlx.accounts.model.OAuthType;
import com.mlx.accounts.model.entity.OAuthTokenEntity;

import java.util.List;

/**
 * 9/24/14.
 */
public interface OAuthTokenDao<T extends OAuthTokenEntity> extends Dao<T, Long> {
    List<OAuthTokenEntity> getByAccountAndType(Long accountId, OAuthType type);

    List<OAuthTokenEntity> getByAccount(Long accountId);

    OAuthTokenEntity lastByOAuthUserId(String userId, OAuthType type);
}
