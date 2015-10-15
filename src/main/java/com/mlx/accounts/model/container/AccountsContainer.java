package com.mlx.accounts.model.container;

import com.mlx.accounts.model.Account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Set;

/**
 * <p>
 * 9/8/14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountsContainer implements Serializable {
    private Long total;
    private Set<Account> result;

    public AccountsContainer() {
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Set<Account> getResult() {
        return result;
    }

    public void setResult(Set<Account> result) {
        this.result = result;
    }
}
