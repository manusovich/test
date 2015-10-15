package com.mlx.accounts.model.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * 9/8/14.
 */
@Embeddable
public class PasswordEntity implements Serializable {
    private String passwordHash;
    private Long passwordCreated = (long) System.currentTimeMillis();
    private Long passwordLifeTime = (long) 1000 * 60 * 60 * 24 * 365 * 10; // 10 years

    public PasswordEntity() {
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Long getPasswordCreated() {
        return passwordCreated;
    }

    public void setPasswordCreated(Long created) {
        this.passwordCreated = created;
    }

    public Long getPasswordLifeTime() {
        return passwordLifeTime;
    }

    public void setPasswordLifeTime(Long lifeTime) {
        this.passwordLifeTime = lifeTime;
    }
}
