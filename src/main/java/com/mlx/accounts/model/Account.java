package com.mlx.accounts.model;

import com.mlx.accounts.exception.UnauthorizedException;
import com.mlx.accounts.model.entity.AccountEntity;
import com.mlx.accounts.model.entity.UserRoles;
import jodd.bean.BeanCopy;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.Column;
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
public class Account implements Serializable {
    private String uid;
    @XmlElement(required = false)
    private String email;
    @XmlElement(required = false)
    private String userName;
    @XmlElement(required = false)
    private String company;
    @XmlElement(required = false)
    private String occupation;
    @XmlElement(required = false)
    private Boolean linkedInConnect;
    @XmlElement(required = false)
    private Boolean facebookConnect;
    @XmlElement(required = false)
    private Boolean githubConnect;
    @XmlElement(required = false)
    private Boolean googleConnect;
    @XmlElement(required = false)
    private Boolean passwordIsNotDefined;
    @XmlElement(required = false)
    private String picture;
    @XmlElement(required = false)
    private String pictureProvider;
    @Column(length = 65535, columnDefinition = "TEXT")
    private String about;
    @XmlElement(required = false)
    private String role;
    @XmlElement(required = false)
    private Long points;
    @XmlElement(required = false)
    private Long pointsHolded;
    @XmlElement(required = false)
    private Long unreadMessages;
    @XmlElement(required = false)
    private boolean bestMatch;

    public Account() {
    }

    public Account(AccountEntity entity) {
        BeanCopy.beans(entity, this).copy();

        setPasswordIsNotDefined(entity.getPassword() == null
                || entity.getPassword().getPasswordHash() == null
                || entity.getPassword().getPasswordHash().isEmpty());

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
        this.userName = StringEscapeUtils.escapeHtml3(userName);
    }

    public Boolean getLinkedInConnect() {
        return linkedInConnect;
    }

    public void setLinkedInConnect(Boolean linkedInConnect) {
        this.linkedInConnect = linkedInConnect;
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
        this.about = StringEscapeUtils.escapeHtml3(about);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public Long getPointsHolded() {
        return pointsHolded;
    }

    public void setPointsHolded(Long pointsHolded) {
        this.pointsHolded = pointsHolded;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Boolean getPasswordIsNotDefined() {
        return passwordIsNotDefined;
    }

    public void setPasswordIsNotDefined(Boolean passwordIsNotDefined) {
        this.passwordIsNotDefined = passwordIsNotDefined;
    }

    public Long getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(Long unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public Boolean getFacebookConnect() {
        return facebookConnect;
    }

    public void setFacebookConnect(Boolean facebookConnect) {
        this.facebookConnect = facebookConnect;
    }

    public Boolean getGithubConnect() {
        return githubConnect;
    }

    public void setGithubConnect(Boolean githubConnect) {
        this.githubConnect = githubConnect;
    }

    public Boolean getGoogleConnect() {
        return googleConnect;
    }

    public void setGoogleConnect(Boolean googleConnect) {
        this.googleConnect = googleConnect;
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

    public String getPictureProvider() {
        return pictureProvider;
    }

    public void setPictureProvider(String pictureProvider) {
        this.pictureProvider = pictureProvider;
    }

    public boolean isBestMatch() {
        return bestMatch;
    }

    public void setBestMatch(boolean bestMatch) {
        this.bestMatch = bestMatch;
    }

    public void assertAdmin() throws UnauthorizedException {
        if (!UserRoles.ADMINISTRATOR.equalsIgnoreCase(getRole())) {
            throw new UnauthorizedException("Not enough rights");
        }
    }

    public Account copy() {
        Account account = new Account();
        BeanCopy.beans(this, account).copy();
        return account;
    }
}
