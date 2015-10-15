package com.mlx.accounts.service.impl;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.mlx.accounts.AppConfiguration;
import com.mlx.accounts.exception.ApplicationException;
import com.mlx.accounts.model.*;
import com.mlx.accounts.model.entity.*;
import com.mlx.accounts.oauth.OAuthManager;
import com.mlx.accounts.repository.ApplicationRepository;
import com.mlx.accounts.service.AccountService;
import com.mlx.accounts.service.MetricsService;
import com.mlx.accounts.service.OAuthService;
import com.mlx.accounts.service.StorageService;
import com.mlx.accounts.support.JWT;

import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 9/8/14.
 */
@Singleton
public class OAuthServiceImpl implements OAuthService {
    public static final int MAX_ABOUT_LENGTH = 65000;

    @Inject
    private AccountService accountService;

    @Inject
    private ApplicationRepository repository;

    @Inject
    private MetricsService metricsService;

    @Inject
    private StorageService storageService;

    @Inject
    private OAuthManager oAuthManager;

    @Inject
    private AppConfiguration appConfiguration;

    @Inject
    private JWT jwt;

    @Transactional
    private AccountEntity saveNewAccount(AccountEntity accountEntity) {
        AccountEntity savedEntity;
        /**
         * Create account
         */
        if (isAdminByEmail(accountEntity.getEmail())) {
            accountEntity.setRole(String.valueOf(AccountRole.ADMINISTRATOR));
        } else {
            accountEntity.setRole(String.valueOf(AccountRole.USER));
        }

        savedEntity = getRepository().getAccountDao().create(accountEntity);
        return savedEntity;
    }

    private boolean isAdminByEmail(String email) {
        return email != null && appConfiguration
                .getAdministrators().contains(email.toLowerCase());
    }


    @Override
    @Transactional
    public OAuthSyncResult handleOAuthSyncKey(AccountEntity caller,
                                              OAuthToken oAuthToken,
                                              OAuthType type) throws ApplicationException {
        OAuthData data = getoAuthManager().byType(type).readUserData(oAuthToken.getToken());

        OAuthSyncResult syncResult = new OAuthSyncResult();
        /**
         * We have to try to find account with the same ID or register new one
         */
        OAuthTokenEntity oldOAuthToken = getRepository()
                .getOAuthTokenDao().lastByOAuthUserId(data.getUserId(), type);

        /**
         * If somebody else has the same OAuth token, we have to interrupt this process.
         */
        if (oldOAuthToken != null && oldOAuthToken.getAccount() != null
                && !oldOAuthToken.getAccount().getId().equals(caller.getId())) {
            syncResult.setAccountExistsConflict(true);
            return syncResult;
        }

        loadUserDataIntoAccount(oAuthToken, caller, data, type);

        getMetricsService().event(new Account(caller),
                getoAuthManager().byType(type).syncMetricType());

        return syncResult;
    }

    private void loadUserDataIntoAccount(OAuthToken oAuthToken,
                                         AccountEntity accountEntity,
                                         OAuthData data,
                                         OAuthType type) throws ApplicationException {


        OAuthTokenEntity token = new OAuthTokenEntity();
        token.setAccount(accountEntity);
        token.setLifetime(oAuthToken.getLifetime() * 1000);
        token.setToken(oAuthToken.getToken());
        token.setUserId(data.getUserId());
        token.setType(String.valueOf(type));

        List<OAuthTokenEntity> keys = getRepository().getOAuthTokenDao()
                .getByAccountAndType(accountEntity.getId(), type);

        getRepository().getOAuthTokenDao().create(token);

        for (OAuthTokenEntity e : keys) {
            getRepository().getOAuthTokenDao().remove(e.getId());
        }

        updateProfile(accountEntity, data);
    }

    @Override
    @Transactional
    public OAuthSession handleOAuthAuthKey(final OAuthToken oAuthToken,
                                           final String ip,
                                           final OAuthType type) throws ApplicationException {
        OAuthData data = getoAuthManager().byType(type).readUserData(oAuthToken.getToken());

        OAuthTokenEntity oldOAuthToken = getRepository()
                .getOAuthTokenDao().lastByOAuthUserId(data.getUserId(), type);

        OAuthSession result = new OAuthSession();

        AccountEntity account;
        if (oldOAuthToken == null || oldOAuthToken.getAccount() == null) {
            result.setNewAccount(true);

            account = new AccountEntity();
            account.setUid(UUID.randomUUID().toString());
            account.setPicture(data.getPicture());
            account.setEmail(data.getEmail());

            String dataTmp = data.getSummary();
            if (dataTmp != null && dataTmp.length() > MAX_ABOUT_LENGTH) {
                dataTmp = dataTmp.substring(0, MAX_ABOUT_LENGTH) + "...";
            }

            account.setAbout(dataTmp);

            String name = data.getFirstName();
            if (data.getLastName() != null && !data.getLastName().isEmpty()) {
                name = name + " " + data.getLastName();
            }
            account.setUserName(name);
            account.setStatus(AccountStatus.VERIFIED);

            account = saveNewAccount(account);
        } else {
            account = oldOAuthToken.getAccount();
        }

        loadUserDataIntoAccount(oAuthToken, account, data, type);

        result.setToken(jwt.generate(account.getUid(), ip));

        getMetricsService().event(new Account(account),
                getoAuthManager().byType(type).authMetricType());

        return result;
    }


    @Override
    @Transactional
    public void syncOAuthDataInProfile(Account caller, OAuthType type) throws ApplicationException {
        AccountEntity accountEntity = repository.getAccountDao().getByUid(caller.getUid());

        OAuthTokenEntity authToken = getValidOAuthCode(accountEntity, type);

        if (authToken == null) {
            throw new ApplicationException("Token is invalid");
        }

        OAuthData oauthData = getoAuthManager().byType(type).readUserData(authToken.getToken());
        updateProfile(accountEntity, oauthData);
    }


    @Transactional
    public void updateProfile(AccountEntity accountEntity,
                              OAuthData oauthData) throws ApplicationException {
        if (accountEntity.getAbout() == null || accountEntity.getAbout().isEmpty()) {
            accountEntity.setAbout(oauthData.getSummary());
        }

        if (accountEntity.getUserName() == null || accountEntity.getUserName().isEmpty()) {
            String firstName = "";
            String lastName = "";
            if (oauthData.getFirstName() != null && !oauthData.getFirstName().isEmpty()) {
                firstName = oauthData.getFirstName();
            }
            if (oauthData.getLastName() != null && !oauthData.getLastName().isEmpty()) {
                lastName = oauthData.getLastName();
            }

            if (!firstName.isEmpty() && !lastName.isEmpty()) {
                accountEntity.setUserName(firstName + " " + lastName);
            } else {
                accountEntity.setUserName(firstName + lastName);
            }
        }

        if (accountEntity.getOccupation() == null || accountEntity.getOccupation().isEmpty()) {
            accountEntity.setOccupation(oauthData.getOccupation());
        }

        if (accountEntity.getCompany() == null || accountEntity.getCompany().isEmpty()) {
            accountEntity.setCompany(oauthData.getCompany());
        }

        if (accountEntity.getEmail() == null || accountEntity.getEmail().isEmpty()) {
            if (oauthData.getEmail() != null) {
                AccountEntity old = getRepository().getAccountDao().getByEmail(oauthData.getEmail());
                if (old != null && !old.getId().equals(accountEntity.getId())) {
                    throw new ApplicationException("Account with such email already exists");
                }
            }
            accountEntity.setEmail(oauthData.getEmail());
        }

        if (accountEntity.getPicture() == null || accountEntity.getPicture().isEmpty()) {
            try {
                if (oauthData.getPicture() != null && !oauthData.getPicture().isEmpty()) {
                    accountEntity.setPicture(getStorageService().saveUserPicture(
                            oauthData.getPicture(), accountEntity.getUid()));
                } else {
                    accountEntity.setPicture(null);
                }
            } catch (Exception e) {
                System.out.println("OAuth data doesn't have picture information");
            }
        }

        getRepository().getAccountDao().update(accountEntity);
        accountService.updateAccountInCache(accountEntity.getUid());
    }

    public OAuthTokenEntity getValidOAuthCode(AccountEntity accountEntity, OAuthType type) {
        List<OAuthTokenEntity> tokens = getRepository()
                .getOAuthTokenDao().getByAccountAndType(accountEntity.getId(), type);

        OAuthTokenEntity authToken = null;
        if (tokens != null && tokens.size() > 0) {
            for (OAuthTokenEntity tokenEntity : tokens) {
                if (System.currentTimeMillis() < tokenEntity.getCreated() + tokenEntity.getLifetime()) {
                    authToken = tokenEntity;
                    break;
                }
            }
        }
        return authToken;
    }

    public VerificationCodeEntity clearVerificationCode(String token) throws ApplicationException {

        VerificationCodeEntity verificationCodeEntity = getRepository()
                .getVerificationCodeDao().getByCode(token);
        verificationCodeEntity.expirationCheck();
        verificationCodeEntity.checkType(
                VerificationCodeTypes.LINKEDIN_AUTH, VerificationCodeTypes.LINKEDIN_SYNC,
                VerificationCodeTypes.GOOGLE_AUTH, VerificationCodeTypes.GOOGLE_SYNC,
                VerificationCodeTypes.FACEBOOK_AUTH, VerificationCodeTypes.FACEBOOK_SYNC,
                VerificationCodeTypes.GITHUB_AUTH, VerificationCodeTypes.GITHUB_SYNC);
        verificationCodeEntity.setLifetime((long) 0);

        getRepository().getVerificationCodeDao().update(verificationCodeEntity);

        return verificationCodeEntity;
    }

    public VerificationCodeEntity createVerificationCode(
            final VerificationCodeTypes type,
            final String data,
            final String forward,
            final String ip) {
        VerificationCodeEntity verificationCodeEntity = new VerificationCodeEntity();
        verificationCodeEntity.setType(String.valueOf(type));
        verificationCodeEntity.setData(data);
        verificationCodeEntity.setForward(forward);
        verificationCodeEntity.setIp(ip);

        getRepository().getVerificationCodeDao().create(verificationCodeEntity);

        return verificationCodeEntity;
    }

    public String oAuthTokenResponseURL(final OAuthOperationResult session) {
        String url = appConfiguration.getOauthConfiguration().getSuccess();

        if (session != null
                && session.getForward() != null
                && !session.getForward().isEmpty()
                && (
                session.getForward().matches("^/account$")
                        || session.getForward().matches("^/account/settings$")
                        || session.getForward().matches("^/account/messages/[\\w-]+$")
        )) {
            url = url + session.getForward();
        } else {
            url = url + "/people";
        }

        if (session instanceof OAuthSyncResult
                && ((OAuthSyncResult) session).isAccountExistsConflict()) {
            url = url + "?e=dlk";
        }

        return url;
    }

    @Override
    public String clientIdByOAuthType(OAuthType type) throws ApplicationException {
        return getoAuthManager().byType(type).clientCode();
    }

    @Override
    @Transactional
    public String authorizationUrl(final OAuthType type, final boolean auth,
                                   final Account account, final String forward,
                                   final String ip) throws ApplicationException {

        VerificationCodeTypes verificationType;
        if (auth) {
            verificationType = getoAuthManager().byType(type).authValidationCodeType();
        } else {
            verificationType = getoAuthManager().byType(type).syncValidationCodeType();
        }

        VerificationCodeEntity verificationCodeEntity = createVerificationCode(
                verificationType,
                account != null ? account.getUid() : null,
                forward,
                ip);

        return getoAuthManager().byType(type).authorizationUrl(
                verificationCodeEntity.getVerificationCode());
    }

    public OAuthOperationResult processOAuth(final String verificationToken, final String oAuthToken) throws ApplicationException {

        VerificationCodeEntity verificationCodeEntity = clearVerificationCode(verificationToken);
        VerificationCodeTypes verificationCodeType = VerificationCodeTypes.valueOf(verificationCodeEntity.getType());
        OAuthType oAuthType = byVerificationType(verificationCodeType);

        OAuthToken token = getoAuthManager().byType(oAuthType).readAccessToken(oAuthToken);
        if (token != null) {
            switch (verificationCodeType) {
                case LINKEDIN_AUTH:
                case FACEBOOK_AUTH:
                case GOOGLE_AUTH:
                case GITHUB_AUTH:
                    OAuthSession session;
                    session = handleOAuthAuthKey(token, verificationCodeEntity.getIp(), oAuthType);
                    session.setForward(verificationCodeEntity.getForward());
                    return session;
                case LINKEDIN_SYNC:
                case FACEBOOK_SYNC:
                case GOOGLE_SYNC:
                case GITHUB_SYNC:
                    AccountEntity account = repository.getAccountDao().getByUid(verificationCodeEntity.getData());
                    OAuthSyncResult syncResult = handleOAuthSyncKey(account, token, oAuthType);
                    syncResult.setForward(verificationCodeEntity.getForward());
                    return syncResult;
            }
        }

        return null;
    }

    public OAuthType byVerificationType(VerificationCodeTypes type) {
        switch (type) {
            case LINKEDIN_AUTH:
            case LINKEDIN_SYNC:
                return OAuthType.LINKEDIN;
            case FACEBOOK_AUTH:
            case FACEBOOK_SYNC:
                return OAuthType.FACEBOOK;
            case GOOGLE_AUTH:
            case GOOGLE_SYNC:
                return OAuthType.GOOGLE;
            case GITHUB_AUTH:
            case GITHUB_SYNC:
                return OAuthType.GITHUB;
        }
        return null;
    }


    public ApplicationRepository getRepository() {
        return repository;
    }

    public void setRepository(ApplicationRepository repository) {
        this.repository = repository;
    }

    public MetricsService getMetricsService() {
        return metricsService;
    }

    public StorageService getStorageService() {
        return storageService;
    }

    public OAuthManager getoAuthManager() {
        return oAuthManager;
    }
}
