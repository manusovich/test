package com.mlx.accounts.support;

import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Signer;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenClaim;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenHeader;
import com.mlx.accounts.AppConfiguration;
import com.mlx.accounts.exception.ApplicationException;
import com.mlx.accounts.model.Token;
import org.joda.time.DateTime;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.security.SecureRandom;

/**
 * 9/1/15.
 */
@Singleton
public class JWT {
    @Inject
    private AppConfiguration configuration;

    private SecureRandom random = new SecureRandom();

    public synchronized Token generate(String uid, String host) throws ApplicationException {
        String authenticationSecret = configuration.getJwtConfiguration().getAuthenticationSecret();

        final HmacSHA512Signer signer =
                new HmacSHA512Signer(authenticationSecret.getBytes());

        DateTime expiration = new DateTime().plusYears(1);
        final JsonWebToken token = JsonWebToken.builder()
                .header(JsonWebTokenHeader.HS512())
                .claim(JsonWebTokenClaim.builder()
                        .subject(uid)
                        .param("r", random.generateSeed(8))
                        .param("h", host)
                        .param("a", "auth")
                        .issuedAt(new DateTime())
                        .expiration(expiration)
                        .build())
                .build();

        Token t = new Token();
        t.setToken(signer.sign(token));
        t.setExpiration(expiration);
        return t;
    }

    public synchronized String buildToken(Data data) throws JoseException {
        JwtClaims claims = new JwtClaims();
        claims.setIssuer(configuration.getJwtConfiguration().getIssuer());  // who creates the token and signs it
        claims.setAudience(configuration.getJwtConfiguration().getAudience()); // to whom the token is intended to be sent
        claims.setExpirationTimeMinutesInTheFuture(86400); // time when the token will expire (60 days from now)
        claims.setGeneratedJwtId(); // a unique identifier for the token
        claims.setIssuedAtToNow();  // when the token was issued/created (now)
        claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
        claims.setSubject(data.getPrincipal()); // the subject/principal is whom the token is about
        claims.setClaim("action", data.getAction()); // additional claims/attributes about the subject can be added
        claims.setClaim("value", data.getValue()); // additional claims/attributes about the subject can be added

        // A JWT is a JWS and/or a JWE with JSON claims as the payload.
        // In this example it is a JWS so we create a JsonWebSignature object.
        JsonWebSignature jws = new JsonWebSignature();

        // The payload of the JWS is JSON content of the JWT Claims
        jws.setPayload(claims.toJson());

        // The JWT is signed using the private key
        jws.setKey(key().getRsaPrivateKey());

        // Set the Key ID (kid) header because it's just the polite thing to do.
        // We only have one key in this example but a using a Key ID helps
        // facilitate a smooth key rollover process
        jws.setKeyIdHeaderValue(configuration.getJwtConfiguration().getKeyId());

        // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA512);

        // Sign the JWS and produce the compact serialization or the complete JWT/JWS
        // representation, which is a string consisting of three dot ('.') separated
        // base64url-encoded parts in the form Header.Payload.Signature
        // If you wanted to encrypt it, you can simply set this jwt as the payload
        // of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
        return jws.getCompactSerialization();

//        JsonWebEncryption jwe = new JsonWebEncryption();
//        jwe.setPayload(payload);
//        jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
//        jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
//        jwe.setKey(key());
//        return jwe.getCompactSerialization();
    }

    public synchronized Data extractPayload(String token) throws JoseException, InvalidJwtException, MalformedClaimException {
        // Use JwtConsumerBuilder to construct an appropriate JwtConsumer, which will
        // be used to validate and process the JWT.
        // The specific validation requirements for a JWT are context dependent, however,
        // it typically advisable to require a expiration time, a trusted issuer, and
        // and audience that identifies your system as the intended recipient.
        // If the JWT is encrypted too, you need only provide a decryption key or
        // decryption key resolver to the builder.
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime() // the JWT must have an expiration time
                .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
                .setRequireSubject() // the JWT must have a subject claim
                .setExpectedIssuer(configuration.getJwtConfiguration().getIssuer()) // whom the JWT needs to have been issued by
                .setExpectedAudience(configuration.getJwtConfiguration().getAudience()) // to whom the JWT is intended for
                .setVerificationKey(key().getRsaPublicKey()) // verify the signature with the public key
                .build(); // create the JwtConsumer instance


        //  Validate the JWT and process it to the Claims
        JwtClaims jwtClaims = jwtConsumer.processToClaims(token);

        String action = jwtClaims.getStringClaimValue("action");
        String value = jwtClaims.getStringClaimValue("value");

        return new Data(jwtClaims.getSubject(), action, value);
    }

    private RsaJsonWebKey key() throws JoseException {
        return (RsaJsonWebKey) PublicJsonWebKey.Factory.newPublicJwk(
                configuration.getJwtConfiguration().getEncryptionKey());
    }

    public static class Data {
        private String principal, action, value;

        public Data(String principal, String key, String value) {
            this.principal = principal;
            this.action = key;
            this.value = value;
        }

        public String getPrincipal() {
            return principal;
        }

        public void setPrincipal(String principal) {
            this.principal = principal;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
