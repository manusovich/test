package com.mlx.accounts.notification.builder;

import com.mlx.accounts.model.MailAddress;
import com.mlx.accounts.model.entity.AccountEntity;
import com.mlx.accounts.notification.NotificationEmail;
import com.mlx.accounts.notification.NotificationEmailSimple;
import com.mlx.accounts.notification.NotificationEmailTemplate;

/**
 * 1/10/15.
 */
public class NotificationEmailSimpleBuilder {
    private NotificationEmailSimple notification;

    public NotificationEmailSimpleBuilder(NotificationEmail notification, AccountEntity accountEntity) {
        this.notification = new NotificationEmailSimple(notification);
        this.notification.setReceiver(accountEntity);
        this.notification.setTo(getMailRecipient(accountEntity));
        this.notification.setTemplate(NotificationEmailTemplate.DEFAULT);
    }

    public NotificationEmailSimpleBuilder(NotificationEmail notification, String email) {
        this.notification = new NotificationEmailSimple(notification);

        MailAddress mailRecipient = new MailAddress();
        mailRecipient.setEmail(email);

        this.notification.setTo(mailRecipient);
        this.notification.setTemplate(NotificationEmailTemplate.DEFAULT);
    }

    private String getFullName(AccountEntity accountEntity) {
        String fullName = "";
        if (accountEntity.getUserName() != null && !accountEntity.getUserName().isEmpty()) {
            fullName = accountEntity.getUserName();
        }
        return fullName;
    }

    private MailAddress getMailRecipient(AccountEntity recipient) {
        MailAddress mailRecipient = new MailAddress();
        mailRecipient.setName(getFullName(recipient));
        mailRecipient.setEmail(recipient.getEmail());
        return mailRecipient;
    }

    public NotificationEmailSimpleBuilder message(String message) {
        notification.setMessage(message);
        return this;
    }

    public NotificationEmailSimple build() {
        return notification;
    }

}
