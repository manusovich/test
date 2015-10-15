package com.mlx.accounts.repository;

import com.mlx.accounts.model.entity.AccountEntity;

import java.util.List;
import java.util.Map;

/**
 * 9/24/14.
 */
public interface AccountDao<T extends AccountEntity> extends Dao<T, Long> {

    AccountEntity create(AccountEntity entity);

    AccountEntity update(AccountEntity entity);

    AccountEntity getByEmail(String email);

    List<AccountEntity> getAllByEmail(String email);

    AccountEntity getByUid(String uid);

    List<String> read(long count, long page, Map<String, String> sort);

    Object count();
}
