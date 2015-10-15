package com.mlx.accounts.repository.impl;

import com.google.inject.Singleton;
import com.mlx.accounts.model.entity.VerificationCodeEntity;
import com.mlx.accounts.repository.VerificationCodeDao;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * <p>
 * 9/8/14.
 */
@Singleton
public class VerificationCodeDaoImpl extends GenericDaoImpl<VerificationCodeEntity, Long>
        implements VerificationCodeDao<VerificationCodeEntity> {

    @Override
    public VerificationCodeEntity read(Long id) {
        return getEntityManager().find(VerificationCodeEntity.class, id);
    }

    @Override
    public List<VerificationCodeEntity> readAll() {
        TypedQuery<VerificationCodeEntity> query = getEntityManager().createQuery(
                "select a from VerificationCodeEntity a", VerificationCodeEntity.class);
        return query.getResultList();
    }

    @Override
    public VerificationCodeEntity getByCode(String code) {
        TypedQuery<VerificationCodeEntity> query = getEntityManager().createQuery(
                "select a from VerificationCodeEntity a where a.verificationCode = :code", VerificationCodeEntity.class);
        query.setParameter("code", code);
        return query.getSingleResult();
    }

    @Override
    public List<VerificationCodeEntity> getByAccountId(Long accountId) {
        TypedQuery<VerificationCodeEntity> query = getEntityManager().createQuery(
                "select a from VerificationCodeEntity a where a.account.id = :accountId", VerificationCodeEntity.class);
        query.setParameter("accountId", accountId);
        return query.getResultList();
    }

    @Override
    public void remove(Long id) {
        VerificationCodeEntity code = getEntityManager().find(VerificationCodeEntity.class, id);
        getEntityManager().remove(code);
    }

}
