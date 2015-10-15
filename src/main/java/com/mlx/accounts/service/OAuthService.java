package com.mlx.accounts.service;

import com.mlx.accounts.exception.ApplicationException;
import com.mlx.accounts.model.*;
import com.mlx.accounts.model.entity.AccountEntity;
import com.mlx.accounts.model.entity.OAuthTokenEntity;
import com.mlx.accounts.model.entity.VerificationCodeEntity;

/**
 * <p>
 * 9/8/14.
 */
public interface OAuthService {
    OAuthSyncResult handleOAuthSyncKey(AccountEntity caller, OAuthToken oAuthToken, OAuthType type) throws ApplicationException;

    OAuthSession handleOAuthAuthKey(OAuthToken oAuthToken, String ip, OAuthType type) throws ApplicationException;

    void syncOAuthDataInProfile(Account caller, OAuthType type) throws ApplicationException;

    void updateProfile(AccountEntity accountEntity,
                       OAuthData oauthData) throws ApplicationException;

    OAuthTokenEntity getValidOAuthCode(AccountEntity accountEntity, OAuthType type);

    VerificationCodeEntity clearVerificationCode(String token) throws ApplicationException;

    VerificationCodeEntity createVerificationCode(VerificationCodeTypes type, String data, String forward, String ip);

    String oAuthTokenResponseURL(final OAuthOperationResult session);

    OAuthOperationResult processOAuth(String verificationToken, String oAuthToken) throws ApplicationException;

    String clientIdByOAuthType(OAuthType type) throws ApplicationException;

    String authorizationUrl(final OAuthType type, final boolean auth, final Account caller,
                            final String forward, final String ip) throws ApplicationException;
}
