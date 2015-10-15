package com.mlx.accounts.model;

import org.hibernate.validator.constraints.Length;

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
public class NewPassword implements Serializable {
    @NotNull(message = "Code can't be empty")
    @XmlElement(name = "code")
    @Length(min = 16, max = 2000, message = "Code must be between 16 and 2000 characters.")
    private String code;

    @NotNull(message = "Password can't be empty")
    @XmlElement(name = "password")
    private String password;

    public NewPassword() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        if (!(o instanceof NewPassword)) return false;

        NewPassword that = (NewPassword) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
