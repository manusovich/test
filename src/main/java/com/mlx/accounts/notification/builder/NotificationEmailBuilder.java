package com.mlx.accounts.notification.builder;

import com.mlx.accounts.model.MailAddress;
import com.mlx.accounts.model.Notification;
import com.mlx.accounts.model.entity.AccountEntity;
import com.mlx.accounts.notification.NotificationEmail;

/**
 * 1/10/15.
 */
public class NotificationEmailBuilder {
    private NotificationEmail notification = new NotificationEmail();

    public NotificationEmailBuilder subject(String subject) {
        notification.setSubject(subject);
        return this;
    }

    public NotificationEmailBuilder title(String title) {
        notification.setTitle(title);
        return this;
    }

    protected NotificationEmailBuilder to(MailAddress to) {
        notification.setTo(to);
        return this;
    }

    protected NotificationEmailBuilder cc(MailAddress cc) {
        notification.setCc(cc);
        return this;
    }

    protected NotificationEmailBuilder replyTo(MailAddress replyTo) {
        notification.setReplyTo(replyTo);
        return this;
    }

    public NotificationEmailSimpleBuilder simple(AccountEntity accountEntity) {
        return new NotificationEmailSimpleBuilder(notification, accountEntity);
    }

    public NotificationEmailSimpleBuilder simple(String email) {
        return new NotificationEmailSimpleBuilder(notification, email);
    }

    public Notification build() {
        return notification;
    }
}
