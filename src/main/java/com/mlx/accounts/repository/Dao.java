package com.mlx.accounts.repository;

import com.mlx.accounts.model.entity.ModelEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Contact for all data access objects. Can be implemented by different provides
 * <p>
 * 9/8/14.
 */
public interface Dao<T extends ModelEntity, K extends Serializable> {

    T create(T entity);

    T update(T entity);

    T read(K id);

    List<T> readAll();

    void remove(K id);
}
