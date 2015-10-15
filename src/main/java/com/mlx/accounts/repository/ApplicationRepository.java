package com.mlx.accounts.repository;

import com.google.inject.Inject;
import com.mlx.accounts.repository.impl.*;

/**
 * Postgree Repository Implementation
 * <p>
 * 9/8/14.
 */
public class ApplicationRepository {
    @Inject
    private SessionDaoImpl sessionDao;

    @Inject
    private VerificationCodeDaoImpl verificationCodeDao;

    @Inject
    private AccountDaoImpl accountDao;

    @Inject
    private EventsDaoImpl eventsDao;

    @Inject
    private OAuthTokenDaoImpl oAuthTokenDao;

    public SessionDaoImpl getSessionDao() {
        return sessionDao;
    }

    public void setSessionDao(SessionDaoImpl sessionDao) {
        this.sessionDao = sessionDao;
    }

    public AccountDaoImpl getAccountDao() {
        return accountDao;
    }

    public VerificationCodeDaoImpl getVerificationCodeDao() {
        return verificationCodeDao;
    }

    public void setVerificationCodeDao(VerificationCodeDaoImpl verificationCodeDao) {
        this.verificationCodeDao = verificationCodeDao;
    }

    public EventsDaoImpl getEventsDao() {
        return eventsDao;
    }

    public void setEventsDao(EventsDaoImpl eventsDao) {
        this.eventsDao = eventsDao;
    }

    public OAuthTokenDaoImpl getOAuthTokenDao() {
        return oAuthTokenDao;
    }

    public void setoAuthTokenDao(OAuthTokenDaoImpl oAuthTokenDao) {
        this.oAuthTokenDao = oAuthTokenDao;
    }
}
