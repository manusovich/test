package com.mlx.accounts.health;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.Singleton;
import com.mlx.accounts.repository.ApplicationRepository;

import javax.inject.Inject;

/**
 * 9/7/15.
 */
@Singleton
public class DBHealthCheck extends HealthCheck {
    @Inject
    private ApplicationRepository repository;

    @Override
    protected Result check() throws Exception {
        Long count = repository.getEventsDao().count();
        return count != null && count > 0 ? Result.healthy() : Result.unhealthy("Events count is not valid");
    }
}
