package com.mlx.accounts.repository.impl;

import com.google.inject.Singleton;
import com.mlx.accounts.model.entity.AccountEntity;
import com.mlx.accounts.model.entity.AccountEntity_;
import com.mlx.accounts.repository.AccountDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 9/8/14.
 */
@Singleton
public class AccountDaoImpl extends GenericDaoImpl<AccountEntity, Long> implements AccountDao<AccountEntity> {
    private static final Logger logger = LoggerFactory.getLogger(AccountDaoImpl.class);


    @Override
    public AccountEntity getByEmail(String email) {
        TypedQuery<AccountEntity> query = getEntityManager().createQuery(
                "select a from AccountEntity a where a.email = :email", AccountEntity.class);
        query.setParameter("email", email.toLowerCase());

        try {
            return query.getSingleResult();
        } catch (NoResultException noResults) {
            return null;
        }
    }

    @Override
    public List<AccountEntity> getAllByEmail(String email) {
        TypedQuery<AccountEntity> query = getEntityManager().createQuery(
                "select a from AccountEntity a where a.email = :email", AccountEntity.class);
        query.setParameter("email", email.toLowerCase());

        return query.getResultList();
    }

    @Override
    public AccountEntity getByUid(String uid) {
        TypedQuery<AccountEntity> query = getEntityManager().createQuery(
                "select a from AccountEntity a where a.uid = :uid", AccountEntity.class);
        query.setParameter("uid", uid);

        AccountEntity result = null;
        try {
            result = query.getSingleResult();
        } catch (NoResultException noResults) {
            logger.warn("No account for " + uid);
        }
        return result;
    }

    @Override
    public AccountEntity read(Long id) {
        return getEntityManager().find(AccountEntity.class, id);
    }

    @Override
    public void remove(Long id) {
        AccountEntity account = getEntityManager().find(AccountEntity.class, id);
        getEntityManager().remove(account);
    }

    @Override
    public List<AccountEntity> readAll() {
        TypedQuery<AccountEntity> query = getEntityManager().createQuery(
                "select a from AccountEntity a", AccountEntity.class);
        return query.getResultList();
    }

    @Override
    public Long count() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(AccountEntity.class)));
        return getEntityManager().createQuery(cq).getSingleResult();
    }

    @Override
    public List<String> read(long count, long page, Map<String, String> sort) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root root = cq.from(AccountEntity.class);
        cq.select(root.get(AccountEntity_.uid));

        if (sort != null && sort.size() > 0) {
            List<Order> orders = new ArrayList<Order>();
            for (String c : sort.keySet()) {
                if ("asc".equalsIgnoreCase(sort.get(c))) {
                    orders.add(cb.asc(root.get(c)));
                } else {
                    orders.add(cb.desc(root.get(c)));
                }
            }
            cq.orderBy(orders);
        }

        cq.where(
                cb.notEqual(
                        root.get(AccountEntity_.uid), "")
        );

        TypedQuery q = getEntityManager().createQuery(cq);
        q.setFirstResult((int) ((page - 1) * count));
        q.setMaxResults((int) count);

        return q.getResultList();
    }
}
