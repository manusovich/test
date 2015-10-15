package com.mlx.accounts.model.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 9/25/14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivationResult {
    private String sessionKey;
    private AccountEntity account;

    public ActivationResult() {
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActivationResult)) return false;

        ActivationResult that = (ActivationResult) o;

        if (account != null ? !account.equals(that.account) : that.account != null) return false;
        if (sessionKey != null ? !sessionKey.equals(that.sessionKey) : that.sessionKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sessionKey != null ? sessionKey.hashCode() : 0;
        result = 31 * result + (account != null ? account.hashCode() : 0);
        return result;
    }
}
