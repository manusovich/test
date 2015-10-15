package com.mlx.accounts.model;

import com.mlx.accounts.model.entity.AccountEntity;
import jodd.bean.BeanCopy;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.Serializable;

public class AccountShort implements Serializable {
    private Long id;
    private String uid;
    private String userName;
    private String picture;

    public AccountShort() {
    }

    public AccountShort(AccountEntity entity) {
        BeanCopy.beans(entity, this).copy();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = StringEscapeUtils.escapeHtml3(userName);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
