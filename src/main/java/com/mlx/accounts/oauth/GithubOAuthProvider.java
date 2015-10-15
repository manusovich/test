package com.mlx.accounts.oauth;


import com.mlx.accounts.AppConfiguration;
import com.mlx.accounts.exception.ApplicationException;
import com.mlx.accounts.model.MetricType;
import com.mlx.accounts.model.OAuthData;
import com.mlx.accounts.model.OAuthToken;
import com.mlx.accounts.model.VerificationCodeTypes;
import com.mlx.accounts.support.JSONUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

/**
 * 3/4/15.
 */
@Singleton
public class GithubOAuthProvider implements OAuthProvider {
    @Inject
    private AppConfiguration appConfiguration;

    @Override
    public String authorizationUrl(String state) throws ApplicationException {
        return "https://github.com/login/oauth/authorize?client_id="
                + clientCode()
                + "&scope=user"
                + "&response_type=code"
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

        String uri = "https://github.com/login/oauth/access_token?"
                + "code=" + oAuthCode
                + "&redirect_uri=" + appURL + "/api/oauth"
                + "&grant_type=authorization_code"
                + "&client_id=" + clientCode()
                + "&client_secret=" + secretCode();

        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader("Accept", "application/json");
        try {
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String retSrc = EntityUtils.toString(entity);

                JSONObject result = new JSONObject(retSrc);
                String expiresIn = "" + (60 * 60 * 24 * 30);
                String accessToken = result.getString("access_token");

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
            HttpGet httpget = new HttpGet("https://api.github.com/user?access_token=" + token);

            response = httpclient.execute(httpget);
            String json = EntityUtils.toString(response.getEntity());

            try {
                HttpGet httpgetEmails = new HttpGet("https://api.github.com/user/emails?access_token="
                        + token);
                response = httpclient.execute(httpgetEmails);
                String jsonEmails = EntityUtils.toString(response.getEntity());
                String email = (String) new JSONArray(jsonEmails).getJSONObject(0).get("email");
                data.setEmail(email);
            } catch (Exception e) {
                System.out.println("Account doesn't have email");
            }

            JSONObject result = new JSONObject(json);
            data.setUserId(result.getString("id"));
            data.setFirstName(JSONUtils.safeString(result, "name"));

            try {
                String picture = result.getString("avatar_url");
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
        return appConfiguration.getOauthConfiguration().getGithub().getSecret();
    }

    @Override
    public String clientCode() {
        return appConfiguration.getOauthConfiguration().getGithub().getId();
    }

    @Override
    public MetricType authMetricType() {
        return MetricType.SIGNON_GITHUB;
    }

    @Override
    public MetricType syncMetricType() {
        return MetricType.GITHUB_SYNC;
    }

    @Override
    public VerificationCodeTypes syncValidationCodeType() {
        return VerificationCodeTypes.GITHUB_SYNC;
    }

    @Override
    public VerificationCodeTypes authValidationCodeType() {
        return VerificationCodeTypes.GITHUB_AUTH;
    }
}
