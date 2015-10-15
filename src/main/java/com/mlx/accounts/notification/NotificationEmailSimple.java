package com.mlx.accounts.notification;

import com.mlx.accounts.model.entity.AccountEntity;

/**
 * 1/10/15.
 */
public class NotificationEmailSimple extends NotificationEmail {
    private AccountEntity receiver;
    private String message;

    public NotificationEmailSimple(NotificationEmail notificationEmail) {
        super(notificationEmail);
    }

    public AccountEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(AccountEntity receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
