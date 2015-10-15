package com.mlx.accounts.model.entity;

import com.mlx.accounts.model.OAuthType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * 9/8/14.
 */
@Entity(name = "mlx_oauth_token")
public class OAuthTokenEntity implements Serializable, ModelEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String userId;
    private String token;
    private Long created = System.currentTimeMillis();
    private Long lifetime;
    private String type;

    @ManyToOne
    private AccountEntity account;

    public OAuthTokenEntity() {
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getType() {
        if (type == null) {
            return "" + OAuthType.LINKEDIN;
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
