package com.mlx.accounts.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.UUID;

/**
 * 9/8/14.
 */
@Entity(name = "mlx_session")
public class SessionEntity implements Serializable, ModelEntity {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private AccountEntity account;
    private String sessionKey = UUID.randomUUID().toString();
    private String ip;
    private Long created = System.currentTimeMillis();
    private Long lifeTime;

    public SessionEntity() {
    }

    public SessionEntity(AccountEntity account) {
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(Long lifeTime) {
        this.lifeTime = lifeTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
