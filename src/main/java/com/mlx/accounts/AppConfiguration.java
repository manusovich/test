package com.mlx.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.xeiam.dropwizard.sundial.SundialConfiguration;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

public class AppConfiguration extends Configuration {

    @JsonProperty("db")
    private DatabaseConfiguration databaseConfiguration;

    @JsonProperty("mixpanel")
    private MixPanelConfiguration mixPanelConfiguration;

    @JsonProperty("aws")
    private AWSConfiguration AWSConfiguration;

    @JsonProperty("jwt")
    private JWTConfiguration jwtConfiguration;

    @JsonProperty("oauth")
    private OAuthConfiguration oauthConfiguration;

    @NotEmpty
    private String url;

    @NotEmpty
    private String logo;

    @NotEmpty
    private String version;

    @NotEmpty
    private Set<String> administrators;

    @Valid
    @NotNull
    public SundialConfiguration sundialConfiguration = new SundialConfiguration();

    @JsonProperty("sundial")
    public SundialConfiguration getSundialConfiguration() {
        return sundialConfiguration;
    }

    public JWTConfiguration getJwtConfiguration() {
        return jwtConfiguration;
    }

    public void setJwtConfiguration(JWTConfiguration jwtConfiguration) {
        this.jwtConfiguration = jwtConfiguration;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public DatabaseConfiguration getDatabaseConfiguration() {
        return databaseConfiguration;
    }

    public void setDatabaseConfiguration(DatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<String> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(Set<String> administrators) {
        this.administrators = administrators;
    }

    public MixPanelConfiguration getMixPanelConfiguration() {
        return mixPanelConfiguration;
    }

    public void setMixPanelConfiguration(MixPanelConfiguration mixPanelConfiguration) {
        this.mixPanelConfiguration = mixPanelConfiguration;
    }

    public AWSConfiguration getAWSConfiguration() {
        return AWSConfiguration;
    }

    public void setAWSConfiguration(AWSConfiguration AWSConfiguration) {
        this.AWSConfiguration = AWSConfiguration;
    }

    public OAuthConfiguration getOauthConfiguration() {
        return oauthConfiguration;
    }

    public void setOauthConfiguration(OAuthConfiguration oauthConfiguration) {
        this.oauthConfiguration = oauthConfiguration;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public class MixPanelConfiguration {
        @NotNull
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public class DatabaseConfiguration {
        @NotNull
        private String driverClass = null;
        private String user = null;
        private String password = "";
        @NotNull
        private String url = null;
        @NotNull
        private Map<String, String> properties = Maps.newLinkedHashMap();

        public String getDriverClass() {
            return driverClass;
        }

        public void setDriverClass(String driverClass) {
            this.driverClass = driverClass;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Map<String, String> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, String> properties) {
            this.properties = properties;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public class AWSConfiguration {
        @JsonProperty("s3")
        private AmazonS3Configuration s3;

        @JsonProperty("ses")
        private AmazonSESConfiguration ses;

        public AmazonS3Configuration getS3() {
            return s3;
        }

        public void setS3(AmazonS3Configuration s3) {
            this.s3 = s3;
        }

        public AmazonSESConfiguration getSes() {
            return ses;
        }

        public void setSes(AmazonSESConfiguration ses) {
            this.ses = ses;
        }

        public class AmazonS3Configuration {
            private String bucket;
            private String accessKey;
            private String secretKey;
            private String domain;

            public String getBucket() {
                return bucket;
            }

            public void setBucket(String bucket) {
                this.bucket = bucket;
            }

            public String getAccessKey() {
                return accessKey;
            }

            public void setAccessKey(String accessKey) {
                this.accessKey = accessKey;
            }

            public String getSecretKey() {
                return secretKey;
            }

            public void setSecretKey(String secretKey) {
                this.secretKey = secretKey;
            }

            public String getDomain() {
                return domain;
            }

            public void setDomain(String domain) {
                this.domain = domain;
            }
        }

        public class AmazonSESConfiguration {
            private String accessKey;
            private String secretKey;
            private String from;
            private String systemAlertReceiver;

            public String getAccessKey() {
                return accessKey;
            }

            public void setAccessKey(String accessKey) {
                this.accessKey = accessKey;
            }

            public String getSecretKey() {
                return secretKey;
            }

            public void setSecretKey(String secretKey) {
                this.secretKey = secretKey;
            }

            public String getFrom() {
                return from;
            }

            public void setFrom(String from) {
                this.from = from;
            }

            public String getSystemAlertReceiver() {
                return systemAlertReceiver;
            }

            public void setSystemAlertReceiver(String systemAlertReceiver) {
                this.systemAlertReceiver = systemAlertReceiver;
            }

        }
    }

    public class JWTConfiguration {
        private String authenticationSecret;
        private Map<String, Object> encryptionKey;
        private String issuer;
        private String audience;
        private String keyId;

        public String getAuthenticationSecret() {
            return authenticationSecret;
        }

        public void setAuthenticationSecret(String authenticationSecret) {
            this.authenticationSecret = authenticationSecret;
        }

        public Map<String, Object> getEncryptionKey() {
            return encryptionKey;
        }

        public void setEncryptionKey(Map<String, Object> encryptionKey) {
            this.encryptionKey = encryptionKey;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }

        public String getKeyId() {
            return keyId;
        }

        public void setKeyId(String keyId) {
            this.keyId = keyId;
        }
    }

    public class OAuthConfiguration {
        private Credentials github, google, linkedIn, facebook;
        private String success, setup, issue;

        public Credentials getGithub() {
            return github;
        }

        public void setGithub(Credentials github) {
            this.github = github;
        }

        public Credentials getGoogle() {
            return google;
        }

        public void setGoogle(Credentials google) {
            this.google = google;
        }

        public Credentials getLinkedIn() {
            return linkedIn;
        }

        public void setLinkedIn(Credentials linkedIn) {
            this.linkedIn = linkedIn;
        }

        public Credentials getFacebook() {
            return facebook;
        }

        public void setFacebook(Credentials facebook) {
            this.facebook = facebook;
        }

        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
            this.success = success;
        }

        public String getSetup() {
            return setup;
        }

        public void setSetup(String setup) {
            this.setup = setup;
        }

        public String getIssue() {
            return issue;
        }

        public void setIssue(String issue) {
            this.issue = issue;
        }

        public class Credentials {
            private String id;
            private String secret;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getSecret() {
                return secret;
            }

            public void setSecret(String secret) {
                this.secret = secret;
            }
        }
    }

}
