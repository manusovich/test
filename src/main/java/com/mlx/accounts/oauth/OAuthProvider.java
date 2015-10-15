package com.mlx.accounts.oauth;


import com.mlx.accounts.exception.ApplicationException;
import com.mlx.accounts.model.MetricType;
import com.mlx.accounts.model.OAuthData;
import com.mlx.accounts.model.OAuthToken;
import com.mlx.accounts.model.VerificationCodeTypes;

/**
 * 3/4/15.
 */
public interface OAuthProvider {

    String authorizationUrl(final String state) throws ApplicationException;

    OAuthData readUserData(String token) throws ApplicationException;

    OAuthToken readAccessToken(String oAuthCode) throws ApplicationException;

    String secretCode();

    String clientCode();

    MetricType authMetricType();

    MetricType syncMetricType();

    VerificationCodeTypes authValidationCodeType();

    VerificationCodeTypes syncValidationCodeType();
}
