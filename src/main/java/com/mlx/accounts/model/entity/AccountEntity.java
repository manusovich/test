package com.mlx.accounts.model.entity;

import org.apache.commons.lang3.StringEscapeUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 9/8/14.
 */
@Entity(name = "mlx_account")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AccountEntity implements Serializable, ModelEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String uid;
    private String email;
    private String userName;
    private AccountStatus status = AccountStatus.CREATED;
    private PasswordEntity password;
    private String picture;
    private String pictureProvider;
    private String company;
    private String occupation;
    private String about;
    private String role;
    private Long lastTimeCodeChecking = (long) 0;
    private Long amountOfWrongCodeChecking = (long) 0;

    public AccountEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public PasswordEntity getPassword() {
        return password;
    }

    public void setPassword(PasswordEntity password) {
        this.password = password;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }


    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String aboutEscaped() {
        if (about != null && !about.isEmpty()) {
            return StringEscapeUtils.escapeHtml3(about);
        } else {
            return about;
        }
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getLastTimeCodeChecking() {
        if (lastTimeCodeChecking == null) {
            return (long) 0;
        }
        return lastTimeCodeChecking;
    }

    public void setLastTimeCodeChecking(Long lastTimeCodeChecking) {
        this.lastTimeCodeChecking = lastTimeCodeChecking;
    }

    public Long getAmountOfWrongCodeChecking() {
        if (amountOfWrongCodeChecking == null) {
            return (long) 0;
        }
        return amountOfWrongCodeChecking;
    }

    public void setAmountOfWrongCodeChecking(Long amountOfCodeChecking) {
        this.amountOfWrongCodeChecking = amountOfCodeChecking;
    }

    public String getPictureProvider() {
        return pictureProvider;
    }

    public void setPictureProvider(String pictureProvider) {
        this.pictureProvider = pictureProvider;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

}
