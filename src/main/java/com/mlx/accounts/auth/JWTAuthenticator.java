package com.mlx.accounts.auth;

import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.validator.ExpiryValidator;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.mlx.accounts.model.Account;
import com.mlx.accounts.service.AccountService;
import io.dropwizard.auth.Authenticator;

/**
 * 8/21/15.
 */
public class JWTAuthenticator implements Authenticator<JsonWebToken, Account> {
    @Inject
    private AccountService accountService;

    @Override
    public Optional<Account> authenticate(JsonWebToken token) {
        try {
            new ExpiryValidator().validate(token);
            String uid = token.claim().subject();
            Account account = accountService.readCached(uid);
            return Optional.of(account);
        } catch (Throwable th) {
            return Optional.absent();
        }
    }
}
