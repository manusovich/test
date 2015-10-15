package com.mlx.accounts.notification;

import com.mlx.accounts.model.NotificationTemplate;

/**
 * 12/14/14.
 */
public enum NotificationEmailTemplate implements NotificationTemplate {
    DEFAULT("/template/default.ftl");

    private String path;

    NotificationEmailTemplate(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }
}
