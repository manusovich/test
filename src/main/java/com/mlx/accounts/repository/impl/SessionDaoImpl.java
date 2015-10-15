package com.mlx.accounts.repository.impl;

import com.google.inject.Singleton;
import com.mlx.accounts.model.entity.SessionEntity;
import com.mlx.accounts.repository.SessionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 9/8/14.
 */
@Singleton
public class SessionDaoImpl extends GenericDaoImpl<SessionEntity, Long> implements SessionDao<SessionEntity> {
    private static final Logger logger = LoggerFactory.getLogger(SessionDaoImpl.class);

    @Override
    public SessionEntity read(Long id) {
        return getEntityManager().find(SessionEntity.class, id);
    }

    @Override
    public List<SessionEntity> readAll() {
        TypedQuery<SessionEntity> query = getEntityManager().createQuery(
                "select a from SessionEntity a", SessionEntity.class);
        return query.getResultList();
    }

    @Override
    public SessionEntity bySessionKey(String sessionKey) {
        if (sessionKey == null || sessionKey.isEmpty() || "null".equalsIgnoreCase(sessionKey)) {
            return null;
        }

        TypedQuery<SessionEntity> query = getEntityManager().createQuery(
                "select a from SessionEntity a where a.sessionKey = :sessionKey", SessionEntity.class);
        query.setParameter("sessionKey", sessionKey);

        SessionEntity result;
        try {
            result = query.getSingleResult();
        } catch (NoResultException noResults) {
            logger.warn("No session entity for " + sessionKey);
            return null;
        }
        return result;
    }

    @Override
    public void removeUnused() {
        long leaveSessions = 5;
        Map<Long, Long> accounts = new HashMap<Long, Long>();

        Query query = getEntityManager().createNativeQuery(
                "select * from (\n" +
                        "SELECT account_id, count(*) as cnt\n" +
                        "from sessionentity s\n" +
                        "group by account_id) s \n" +
                        "where s.cnt > " + leaveSessions);

        List<Object[]> results = query.getResultList();
        for (Object[] r : results) {
            Long accountId = ((BigInteger) r[0]).longValue();
            Long count = ((BigInteger) r[1]).longValue();
            accounts.put(accountId, count);
        }

        for (Long account : accounts.keySet()) {
            TypedQuery<SessionEntity> query2 = getEntityManager().createQuery(
                    "select s from SessionEntity s where s.account.id = :account order by s.created asc",
                    SessionEntity.class);
            query2.setMaxResults((int) (accounts.get(account) - leaveSessions));
            query2.setParameter("account", account);
            for (SessionEntity sessionEntity : query2.getResultList()) {
                remove(sessionEntity.getId());
            }
        }
    }

    @Override
    public List<SessionEntity> byAccountId(Long id) {
        TypedQuery<SessionEntity> query = getEntityManager().createQuery(
                "select a from SessionEntity a where a.account.id = :id", SessionEntity.class);
        query.setParameter("id", id);
        return query.getResultList();
    }

    @Override
    public void remove(Long id) {
        SessionEntity session = getEntityManager().find(SessionEntity.class, id);
        getEntityManager().remove(session);
    }

}
