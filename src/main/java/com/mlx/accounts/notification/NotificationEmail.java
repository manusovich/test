package com.mlx.accounts.notification;

import com.mlx.accounts.App;
import com.mlx.accounts.AppConfiguration;
import com.mlx.accounts.model.MailAddress;
import com.mlx.accounts.model.Notification;
import com.mlx.accounts.model.NotificationTemplate;
import jodd.bean.BeanCopy;

/**
 * 1/10/15.
 */
public class NotificationEmail implements Notification {
    private String subject;
    private String title;
    private NotificationTemplate template;
    private MailAddress to;
    private MailAddress cc;
    private MailAddress replyTo;

    public NotificationEmail() {
    }

    public NotificationEmail(NotificationEmail notificationEmail) {
        BeanCopy.beans(notificationEmail, this).copy();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public NotificationTemplate getTemplate() {
        return template;
    }

    public void setTemplate(NotificationTemplate template) {
        this.template = template;
    }

    public MailAddress getTo() {
        return to;
    }

    public void setTo(MailAddress to) {
        this.to = to;
    }

    public MailAddress getCc() {
        return cc;
    }

    public void setCc(MailAddress cc) {
        this.cc = cc;
    }

    public MailAddress getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(MailAddress replyTo) {
        this.replyTo = replyTo;
    }

    public String getApplicationURL() {
        return App.getGuice().getInjector()
                .getInstance(AppConfiguration.class).getUrl();
    }

    public String getLogo() {
        return App.getGuice().getInjector()
                .getInstance(AppConfiguration.class).getLogo();
    }
}
