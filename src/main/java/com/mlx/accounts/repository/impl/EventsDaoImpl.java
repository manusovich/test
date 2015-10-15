package com.mlx.accounts.repository.impl;

import com.google.inject.Singleton;
import com.mlx.accounts.model.entity.EventEntity;
import com.mlx.accounts.repository.EventsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class EventsDaoImpl extends GenericDaoImpl<EventEntity, Long> implements EventsDao<EventEntity> {
    private static final Logger logger = LoggerFactory.getLogger(EventsDaoImpl.class);

    @Override
    public EventEntity read(Long id) {
        return getEntityManager().find(EventEntity.class, id);
    }

    @Override
    public List<EventEntity> readAll() {
        TypedQuery<EventEntity> query = getEntityManager().createQuery(
                "select a from EventEntity a", EventEntity.class);
        return query.getResultList();
    }

    @Override
    public List<EventEntity> read(long count, long page, Map<String, String> sort) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EventEntity> cq = cb.createQuery(EventEntity.class);
        Root root = cq.from(EventEntity.class);

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

        TypedQuery q = getEntityManager().createQuery(cq);
        q.setFirstResult((int) ((page - 1) * count));
        q.setMaxResults((int) count);
        return q.getResultList();
    }

    @Override
    public Long count() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(EventEntity.class)));
        return getEntityManager().createQuery(cq).getSingleResult();
    }


    @Override
    public void remove(Long id) {
        EventEntity session = getEntityManager().find(EventEntity.class, id);
        getEntityManager().remove(session);
    }

}
