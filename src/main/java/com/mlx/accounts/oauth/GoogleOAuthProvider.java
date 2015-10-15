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
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

/**
 * 3/4/15.
 */
@Singleton
public class GoogleOAuthProvider implements OAuthProvider {
    @Inject
    private AppConfiguration appConfiguration;

    @Override
    public String authorizationUrl(String state) throws ApplicationException {
        return "https://accounts.google.com/o/oauth2/auth?client_id="
                + clientCode()
                + "&scope=email"
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

        String uri = "https://www.googleapis.com/oauth2/v3/token?"
                + "code=" + oAuthCode
                + "&redirect_uri=" + appURL + "/api/oauth"
                + "&grant_type=authorization_code"
                + "&client_id=" + clientCode()
                + "&client_secret=" + secretCode();

        HttpPost httpPost = new HttpPost(uri);
        try {
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String retSrc = EntityUtils.toString(entity);

                JSONObject result = new JSONObject(retSrc);
                String expiresIn = result.getString("expires_in");
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
            HttpGet httpget = new HttpGet("https://www.googleapis.com/plus/v1/people/me?access_token="
                    + token + "&fields=id,emails,displayName,image");

            response = httpclient.execute(httpget);
            String json = EntityUtils.toString(response.getEntity());

            JSONObject result = new JSONObject(json);
            data.setUserId(result.getString("id"));
            data.setFirstName(JSONUtils.safeString(result, "displayName"));

            try {
                JSONArray emails = result.getJSONArray("emails");
                if (emails != null && emails.length() > 0) {
                    data.setEmail(JSONUtils.safeString(emails.getJSONObject(0), "value"));
                }
            } catch (JSONException e) {
                System.out.println("Account doesn't have emails");
            }

            try {
                String picture = JSONUtils.safePathValue(result, "image", "url");
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
        return appConfiguration.getOauthConfiguration().getGoogle().getSecret();
    }

    @Override
    public String clientCode() {
        return appConfiguration.getOauthConfiguration().getGoogle().getId();
    }

    @Override
    public MetricType authMetricType() {
        return MetricType.SIGNON_GOOGLE;
    }

    @Override
    public MetricType syncMetricType() {
        return MetricType.GOOGLE_SYNC;
    }

    @Override
    public VerificationCodeTypes authValidationCodeType() {
        return VerificationCodeTypes.GOOGLE_AUTH;
    }

    @Override
    public VerificationCodeTypes syncValidationCodeType() {
        return VerificationCodeTypes.GOOGLE_SYNC;
    }
}
