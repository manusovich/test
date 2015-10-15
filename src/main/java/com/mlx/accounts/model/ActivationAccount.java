package com.mlx.accounts.model;

import com.mlx.accounts.model.entity.AccountEntity;
import jodd.bean.BeanCopy;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Bean for Account objects
 * <p>
 * 9/8/14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivationAccount implements Serializable {
    @NotNull(message = "Code can't be empty")
    private String code;

    @NotNull(message = "Name can't be empty")
    private String userName;

    @NotNull(message = "Password can't be empty")
    @XmlElement(name = "password")
    private String password;

    public ActivationAccount() {
    }

    public ActivationAccount(AccountEntity entity) {
        BeanCopy.beans(entity, this).copy();
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActivationAccount)) return false;

        ActivationAccount that = (ActivationAccount) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
