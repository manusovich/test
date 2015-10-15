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
 * *
 */
@Singleton
public class LinkedInOAuthProvider implements OAuthProvider {
    @Inject
    private AppConfiguration appConfiguration;

    @Override
    public String authorizationUrl(String state) throws ApplicationException {
        return "https://www.linkedin.com/uas/oauth2/authorization?response_type=code"
                + "&client_id=" + clientCode()
                + "&scope=r_basicprofile"
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

        String uri = "https://www.linkedin.com/uas/oauth2/accessToken?grant_type=authorization_code"
                + "&code=" + oAuthCode
                + "&redirect_uri=" + appURL + "/api/oauth"
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
            HttpGet httpget = new HttpGet("https://api.linkedin.com/v1/people/~:" +
                    "(id,first-name,last-name,skills,summary,picture-url,positions)");
            httpget.setHeader("Authorization", "Bearer " + token);
            httpget.setHeader("x-li-format", "json");

            response = httpclient.execute(httpget);
            String json = EntityUtils.toString(response.getEntity());

            JSONObject result = new JSONObject(json);
            data.setUserId(result.getString("id"));
            data.setFirstName(JSONUtils.safeString(result, "firstName"));
            data.setLastName(JSONUtils.safeString(result, "lastName"));
            data.setSummary(JSONUtils.safeString(result, "summary"));

            JSONObject positionsBlock = result.getJSONObject("positions");
            if (positionsBlock != null) {
                JSONArray positions = positionsBlock.getJSONArray("values");
                if (positions != null && positions.length() > 0) {
                    JSONObject position = positions.getJSONObject(0);
                    data.setOccupation(JSONUtils.safeString(position, "title"));
                    data.setCompany(JSONUtils.safePathValue(position, "company", "name"));
                    if (data.getSummary() == null || data.getSummary().isEmpty()) {
                        data.setSummary(JSONUtils.safeString(position, "summary"));
                    }
                }
            }

            JSONObject skillsSection;
            try {
                skillsSection = result.getJSONObject("skills");
            } catch (JSONException e) {
                skillsSection = null;
            }

            try {
                String picture = result.getString("pictureUrl");
                if (picture != null && !picture.isEmpty()) {
                    data.setPicture(picture);
                }
            } catch (Exception e) {
                System.out.println("Account doesn't have picture");
            }

            if (skillsSection != null) {
                JSONArray skills = skillsSection.getJSONArray("values");
                if (skills != null && skills.length() > 0) {
                    for (int i = 0; i < skills.length(); i++) {
                        JSONObject skill = (JSONObject) skills.get(i);
                        String name = skill.getJSONObject("skill").getString("name");
                        data.getSkills().add(name);
                    }
                }
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
        return appConfiguration.getOauthConfiguration().getLinkedIn().getSecret();
    }

    @Override
    public String clientCode() {
        return appConfiguration.getOauthConfiguration().getLinkedIn().getId();
    }

    @Override
    public MetricType authMetricType() {
        return MetricType.SIGNON_LINKEDIN;
    }

    @Override
    public MetricType syncMetricType() {
        return MetricType.LINKEDIN_SYNC;
    }

    @Override
    public VerificationCodeTypes authValidationCodeType() {
        return VerificationCodeTypes.LINKEDIN_AUTH;
    }

    @Override
    public VerificationCodeTypes syncValidationCodeType() {
        return VerificationCodeTypes.LINKEDIN_SYNC;
    }
}
