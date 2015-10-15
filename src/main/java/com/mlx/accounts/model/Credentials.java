package com.mlx.accounts.model;

import org.hibernate.validator.constraints.Email;

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
public class Credentials implements Serializable {
    @NotNull(message = "Email can't be empty")
    @Email(message = "Email is not valid")
    private String email;

    @NotNull(message = "Password can't be empty")
    @XmlElement(name = "password")
    private String password;

    @XmlElement(required = false)
    private String fwdurl;

    public Credentials() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFwdurl() {
        return fwdurl;
    }

    public void setFwdurl(String fwdurl) {
        this.fwdurl = fwdurl;
    }
}
