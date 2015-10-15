package com.mlx.accounts.repository.impl;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mlx.accounts.model.entity.ModelEntity;
import com.mlx.accounts.repository.Dao;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * 9/30/14.
 */
public abstract class GenericDaoImpl<T extends ModelEntity, K extends Serializable> implements Dao<T, K> {
    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Override
    public T create(T entity) {
        if (entity.getId() != null) {
            throw new IllegalStateException(
                    "Object has id, use update operation instead create");
        }
        getEntityManager().persist(entity);
        return entity;
    }

    @Override
    public T update(T entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException(
                    "Object has empty id, use create operation instead of update");
        }
        return getEntityManager().merge(entity);
    }

    public EntityManager getEntityManager() {
        return entityManagerProvider.get();
    }
}
