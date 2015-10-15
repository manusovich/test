package com.mlx.accounts.model;

/**
 * 12/20/14.
 */
public class MailAddress {
    private String name;
    private String email;

    public MailAddress() {
    }

    public MailAddress(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
