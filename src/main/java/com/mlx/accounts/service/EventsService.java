package com.mlx.accounts.service;

import com.mlx.accounts.model.Account;
import com.mlx.accounts.model.EventType;
import com.mlx.accounts.model.container.EventsContainer;
import com.mlx.accounts.model.entity.AccountEntity;
import com.mlx.accounts.model.entity.SessionEntity;
import com.mlx.accounts.model.entity.VerificationCodeEntity;

import java.util.Map;

/**
 * <p>
 * 9/8/14.
 */
public interface EventsService {
    EventsContainer getEvents(Long count, Long page, Map<String, String> sort);

    void addEvent(SessionEntity sessionEntity, EventType type, String details);

    void addEvent(AccountEntity account, EventType type, String details);

    void addEvent(Account account, EventType type, String details);

    void addEvent(VerificationCodeEntity verificationEntity, EventType type, String details);

    void addEvent(String email, EventType type, String details);

    void addEvent(SessionEntity sessionEntity, EventType type);

    void addEvent(String email, EventType type);

    long count();

}
