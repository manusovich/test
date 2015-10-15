package com.mlx.accounts.oauth;

import com.google.inject.Inject;
import com.mlx.accounts.model.OAuthType;

import javax.inject.Singleton;

/**
 * 3/4/15.
 */
@Singleton
public class OAuthManager {
    @Inject
    private LinkedInOAuthProvider linkedInOAuthReader;
    @Inject
    private FacebookOAuthProvider facebookOAuthReader;
    @Inject
    private GoogleOAuthProvider googleOAuthReader;
    @Inject
    private GithubOAuthProvider githubOAuthReader;

    public OAuthProvider byType(OAuthType type) {
        switch (type) {
            case LINKEDIN:
                return linkedInOAuthReader;
            case FACEBOOK:
                return facebookOAuthReader;
            case GOOGLE:
                return googleOAuthReader;
            case GITHUB:
                return githubOAuthReader;

        }
        throw new IllegalArgumentException(type + " is not supported");
    }
}
