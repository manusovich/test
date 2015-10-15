package com.mlx.accounts.repository;

import com.mlx.accounts.model.entity.VerificationCodeEntity;

import java.util.List;

/**
 * 9/24/14.
 */
public interface VerificationCodeDao<T extends VerificationCodeEntity> extends Dao<T, Long> {

    VerificationCodeEntity getByCode(String code);

    List<VerificationCodeEntity> getByAccountId(Long accountId);
}
