package com.mlx.accounts.model;

/**
 * 12/30/14.
 */
public enum MetricType {
    REGISTRATION("Registration"),
    ACTIVATION("Activation"),
    SIGNON("Sign On"),
    SIGNON_LINKEDIN("Sign LinkedIn"),
    SIGNON_FACEBOOK("Sign Facebook"),
    SIGNON_GOOGLE("Sign Google"),
    SIGNON_GITHUB("Sign Github"),
    LINKEDIN_SYNC("LinkedIn Synchronization"),
    FACEBOOK_SYNC("Facebook Synchronization"),
    GOOGLE_SYNC("Google Synchronization"),
    GITHUB_SYNC("Github Synchronization");

    private String name;

    MetricType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
