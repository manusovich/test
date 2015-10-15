package com.mlx.accounts.model.entity;

import com.mlx.accounts.exception.ApplicationException;
import com.mlx.accounts.exception.TokenExpiredException;
import com.mlx.accounts.model.VerificationCodeTypes;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.UUID;

/**
 * 9/8/14.
 */
@Entity(name = "mlx_verification_code")
public class VerificationCodeEntity implements Serializable, ModelEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String type;
    private String data;
    private String forward;
    @ManyToOne
    private AccountEntity account;
    private String verificationCode = UUID.randomUUID().toString();
    private Long created = System.currentTimeMillis();
    private Long lifetime = (long) 72 * 60 * 60 * 1000; // 72 hours
    private Long confirmed;
    private String ip;

    public VerificationCodeEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getLifetime() {
        return lifetime;
    }

    public void setLifetime(Long lifetime) {
        this.lifetime = lifetime;
    }

    public Long getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Long confirmed) {
        this.confirmed = confirmed;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void make6digits() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int r = (int) (1 + Math.random() * 8);
            builder = builder.append(r);
        }
        setVerificationCode(builder.toString());
    }

    public void expirationCheck() throws TokenExpiredException {
        if (getCreated() + getLifetime() < System.currentTimeMillis()) {
            throw new TokenExpiredException("Code already expired");
        }
    }

    public void checkType(VerificationCodeTypes... allowedTypes) throws ApplicationException {
        boolean exists = false;
        if (allowedTypes != null && allowedTypes.length > 0
                && getType() != null && !getType().isEmpty()) {
            for (VerificationCodeTypes type : allowedTypes) {
                if (getType().equalsIgnoreCase("" + type)) {
                    exists = true;
                    break;
                }
            }
        }
        if (!exists) {
            throw new ApplicationException("Invalid code");
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getForward() {
        return forward;
    }

    public void setForward(String forward) {
        this.forward = forward;
    }
}
