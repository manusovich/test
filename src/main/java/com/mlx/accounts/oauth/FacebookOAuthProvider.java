package com.mlx.accounts.oauth;


import com.mlx.accounts.AppConfiguration;
import com.mlx.accounts.exception.ApplicationException;
import com.mlx.accounts.model.MetricType;
import com.mlx.accounts.model.OAuthData;
import com.mlx.accounts.model.OAuthToken;
import com.mlx.accounts.model.VerificationCodeTypes;
import com.mlx.accounts.support.JSONUtils;
import com.mlx.accounts.support.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Map;

/**
 * 3/4/15.
 */
@Singleton
public class FacebookOAuthProvider implements OAuthProvider {
    private static final Logger logger = LoggerFactory.getLogger(FacebookOAuthProvider.class);

    @Inject
    private AppConfiguration appConfiguration;


    @Override
    public String authorizationUrl(String state) throws ApplicationException {
        return "https://www.facebook.com/dialog/oauth?client_id="
                + clientCode()
                + "&scope=public_profile,email"
                + "&state=" + state
                + "&redirect_uri=" + appConfiguration.getUrl() + "/api/oauth";
    }

    @Override
    public OAuthToken readAccessToken(String oAuthCode) throws ApplicationException {
        String appURL = appConfiguration.getUrl();
        CloseableHttpResponse response = null;

        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();

        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(globalConfig)
                .build();

        String uri = "https://graph.facebook.com/oauth/access_token?"
                + "code=" + oAuthCode
                + "&redirect_uri=" + appURL + "/api/oauth"
                + "&client_id=" + clientCode()
                + "&client_secret=" + secretCode();
        logger.debug("Read oAuth data: " + uri);

        HttpPost httpPost = new HttpPost(uri);
        try {
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String data = EntityUtils.toString(entity);
                logger.debug("OAuth data: " + data);

                Map<String, String> params = StringUtils.splitQuery(data);
                String expiresIn = params.get("expires");
                String accessToken = params.get("access_token");

                EntityUtils.consume(entity);

                return new OAuthToken(accessToken, Long.valueOf(expiresIn));
            }
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public OAuthData readUserData(String token) throws ApplicationException {
        OAuthData data = new OAuthData();

        CloseableHttpClient httpclient = HttpClients.custom().build();
        CloseableHttpResponse response = null;
        try {
            HttpGet httpget = new HttpGet("https://graph.facebook.com/v2.2/me?access_token="
                    + token + "&fields=id,email,first_name,last_name,picture&format=json&method=get&pretty=0&suppress_http_code=1");

            response = httpclient.execute(httpget);
            String json = EntityUtils.toString(response.getEntity());

            JSONObject result = new JSONObject(json);
            data.setUserId(result.getString("id"));
            data.setFirstName(JSONUtils.safeString(result, "first_name"));
            data.setLastName(JSONUtils.safeString(result, "last_name"));
            data.setEmail(JSONUtils.safeString(result, "email"));

            try {
                String picture = JSONUtils.safePathValue(result, "picture", "data", "url");
                if (picture != null && !picture.isEmpty()) {
                    data.setPicture(picture);
                }
            } catch (Exception e) {
                System.out.println("Account doesn't have picture");
            }

            EntityUtils.consume(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException(e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    @Override
    public String secretCode() {
        return appConfiguration.getOauthConfiguration().getFacebook().getSecret();
    }

    @Override
    public String clientCode() {
        return appConfiguration.getOauthConfiguration().getFacebook().getId();
    }


    @Override
    public MetricType authMetricType() {
        return MetricType.SIGNON_FACEBOOK;
    }

    @Override
    public MetricType syncMetricType() {
        return MetricType.FACEBOOK_SYNC;
    }

    @Override
    public VerificationCodeTypes authValidationCodeType() {
        return VerificationCodeTypes.FACEBOOK_AUTH;
    }

    @Override
    public VerificationCodeTypes syncValidationCodeType() {
        return VerificationCodeTypes.FACEBOOK_SYNC;
    }
}
