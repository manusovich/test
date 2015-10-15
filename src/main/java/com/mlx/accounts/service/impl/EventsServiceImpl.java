package com.mlx.accounts.service.impl;

import com.google.inject.Inject;
import com.mlx.accounts.model.Account;
import com.mlx.accounts.model.Event;
import com.mlx.accounts.model.EventType;
import com.mlx.accounts.model.container.EventsContainer;
import com.mlx.accounts.model.entity.AccountEntity;
import com.mlx.accounts.model.entity.EventEntity;
import com.mlx.accounts.model.entity.SessionEntity;
import com.mlx.accounts.model.entity.VerificationCodeEntity;
import com.mlx.accounts.repository.ApplicationRepository;
import com.mlx.accounts.service.EventsService;

import javax.inject.Singleton;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 9/8/14.
 */
@Singleton
public class EventsServiceImpl implements EventsService {
    @Inject
    private ApplicationRepository repository;

    private static final boolean DISABLED = false;

    @Override
    public void addEvent(SessionEntity sessionEntity, EventType type, String details) {
        if (DISABLED) {
            return;
        }
        EventEntity entity = new EventEntity();
        entity.setSessionId(sessionEntity.getId());
        if (sessionEntity.getAccount() != null) {
            AccountEntity account = sessionEntity.getAccount();
            entity.setAccountId(account.getId());
            entity.setUserEmail(account.getEmail());
            entity.setUserName(account.getUserName());
            entity.setUserRole(account.getRole());
        }
        entity.setType(type.toString());
        entity.setDetails(details);
        getRepository().getEventsDao().create(entity);
    }

    @Override
    public void addEvent(AccountEntity account, EventType type, String details) {
        if (DISABLED) {
            return;
        }
        EventEntity entity = new EventEntity();
        entity.setAccountId(account.getId());
        entity.setUserEmail(account.getEmail());
        entity.setUserName(account.getUserName());
        entity.setUserRole(account.getRole());
        entity.setType(type.toString());
        entity.setDetails(details);

        getRepository().getEventsDao().create(entity);
    }

    @Override
    public void addEvent(Account account, EventType type, String details) {
        if (DISABLED) {
            return;
        }
        EventEntity entity = new EventEntity();
        entity.setUserEmail(account.getEmail());
        entity.setUserName(account.getUserName());
        entity.setUserRole(account.getRole());
        entity.setType(type.toString());
        entity.setDetails(details);

        getRepository().getEventsDao().create(entity);
    }

    public void addEvent(VerificationCodeEntity verificationEntity, EventType type, String details) {
        if (DISABLED) {
            return;
        }
        EventEntity entity = new EventEntity();
        entity.setType(type.toString());
        entity.setDetails("Verification code (" + verificationEntity.getType()
                + ") " + verificationEntity.getVerificationCode() + ": " + details);
        getRepository().getEventsDao().create(entity);
    }

    @Override
    public void addEvent(String email, EventType type, String details) {
        if (DISABLED) {
            return;
        }
        EventEntity entity = new EventEntity();
        entity.setUserEmail(email);
        entity.setType(type.toString());
        entity.setDetails(details);
        getRepository().getEventsDao().create(entity);
    }

    @Override
    public EventsContainer getEvents(Long count, Long page, Map<String, String> sort) {
        Set<Event> result = new LinkedHashSet<>();
        List<EventEntity> entities = getRepository().getEventsDao().read(count, page, sort);
        if (entities != null) {
            result.addAll(entities.stream()
                    .map(Event::new)
                    .collect(Collectors.toList()));
        }

        EventsContainer container = new EventsContainer();
        container.setResult(result);
        container.setTotal(getRepository().getEventsDao().count());
        return container;
    }

    @Override
    public long count() {
        return getRepository().getEventsDao().count();
    }

    @Override
    public void addEvent(SessionEntity sessionEntity, EventType type) {
        addEvent(sessionEntity, type, "");
    }

    @Override
    public void addEvent(String email, EventType type) {
        addEvent(email, type, "");
    }

    public ApplicationRepository getRepository() {
        return repository;
    }

    public void setRepository(ApplicationRepository repository) {
        this.repository = repository;
    }

}
