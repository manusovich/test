package com.mlx.accounts.repository;

import com.mlx.accounts.model.entity.EventEntity;

import java.util.List;
import java.util.Map;

/**
 * 9/24/14.
 */
public interface EventsDao<T extends EventEntity> extends Dao<T, Long> {
    List<EventEntity> read(long count, long page, Map<String, String> sort);

    Long count();
}
