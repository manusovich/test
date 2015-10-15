package com.mlx.accounts.model;

/**
 * 8/4/15.
 */
public class AccountMatch {
    private String uid;
    private int matches;
    private boolean allMatch;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    public boolean isAllMatch() {
        return allMatch;
    }

    public void setAllMatch(boolean allMatch) {
        this.allMatch = allMatch;
    }
}
