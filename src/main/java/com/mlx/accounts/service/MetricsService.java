package com.mlx.accounts.service;

import com.mlx.accounts.model.Account;
import com.mlx.accounts.model.MetricType;

import java.util.Map;

/**
 * <p>
 * 9/8/14.
 */
public interface MetricsService {
    void event(final Account account,
               final MetricType metric);

    void event(final Account account,
               final MetricType metric,
               final Map<String, String> userProperties,
               final Map<String, String> userModifiers);
}
