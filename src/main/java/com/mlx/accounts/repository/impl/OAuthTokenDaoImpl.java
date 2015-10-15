package com.mlx.accounts.repository.impl;

import com.google.inject.Singleton;
import com.mlx.accounts.model.OAuthType;
import com.mlx.accounts.model.entity.OAuthTokenEntity;
import com.mlx.accounts.repository.OAuthTokenDao;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * <p>
 * 9/8/14.
 */
@Singleton
public class OAuthTokenDaoImpl extends GenericDaoImpl<OAuthTokenEntity, Long>
        implements OAuthTokenDao<OAuthTokenEntity> {

    @Override
    public OAuthTokenEntity read(Long id) {
        return getEntityManager().find(OAuthTokenEntity.class, id);
    }

    @Override
    public List<OAuthTokenEntity> readAll() {
        TypedQuery<OAuthTokenEntity> query = getEntityManager().createQuery(
                "select a from OAuthTokenEntity a", OAuthTokenEntity.class);
        return query.getResultList();
    }

    @Override
    public List<OAuthTokenEntity> getByAccountAndType(Long accountId, OAuthType type) {
        TypedQuery<OAuthTokenEntity> query = getEntityManager().createQuery(
                "select a from OAuthTokenEntity a where a.account.id = :accountId and a.type = :type",
                OAuthTokenEntity.class);
        query.setParameter("accountId", accountId);
        query.setParameter("type", "" + type);
        return query.getResultList();
    }

    @Override
    public List<OAuthTokenEntity> getByAccount(Long accountId) {
        TypedQuery<OAuthTokenEntity> query = getEntityManager().createQuery(
                "select a from OAuthTokenEntity a where a.account.id = :accountId",
                OAuthTokenEntity.class);
        query.setParameter("accountId", accountId);
        return query.getResultList();
    }

    @Override
    public OAuthTokenEntity lastByOAuthUserId(String userId, OAuthType type) {
        TypedQuery<OAuthTokenEntity> query = getEntityManager().createQuery(
                "select a from OAuthTokenEntity a where a.userId = :oauthUserId and a.type = :type order by a.created desc",
                OAuthTokenEntity.class);
        query.setMaxResults(1);
        query.setParameter("oauthUserId", userId);
        query.setParameter("type", "" + type);

        List<OAuthTokenEntity> resultList = query.getResultList();
        if (resultList != null && resultList.size() > 0) {
            return resultList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void remove(Long id) {
        OAuthTokenEntity code = getEntityManager()
                .find(OAuthTokenEntity.class, id);
        getEntityManager().remove(code);
    }

}
