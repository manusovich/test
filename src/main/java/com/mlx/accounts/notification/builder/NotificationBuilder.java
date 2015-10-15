package com.mlx.accounts.notification.builder;

/**
 * 1/10/15.
 */
public class NotificationBuilder {

    private NotificationBuilder() {
    }

    public NotificationEmailBuilder email() {
        return new NotificationEmailBuilder();
    }

    public static NotificationBuilder get() {
        return new NotificationBuilder();
    }
}
