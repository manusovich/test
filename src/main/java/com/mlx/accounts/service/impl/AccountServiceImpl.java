package com.mlx.accounts.service.impl;


import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.mlx.accounts.AppConfiguration;
import com.mlx.accounts.exception.*;
import com.mlx.accounts.model.*;
import com.mlx.accounts.model.container.AccountsContainer;
import com.mlx.accounts.model.entity.*;
import com.mlx.accounts.notification.NotificationEmail;
import com.mlx.accounts.notification.builder.NotificationBuilder;
import com.mlx.accounts.repository.ApplicationRepository;
import com.mlx.accounts.service.*;
import com.mlx.accounts.support.JWT;
import org.apache.commons.codec.digest.DigestUtils;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.lang.JoseException;
import org.jsr107.ri.annotations.DefaultGeneratedCacheKey;

import javax.cache.Caching;
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CacheResult;
import javax.inject.Singleton;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 9/8/14.
 */
@Singleton
public class AccountServiceImpl implements AccountService {

    @Inject
    private ApplicationRepository repository;

    @Inject
    private NotificationService notificationService;

    @Inject
    private EventsService eventsService;

    @Inject
    private MetricsService metricsService;

    @Inject
    private OAuthService oAuthService;

    @Inject
    private AppConfiguration appConfiguration;

    @Inject
    private JWT jwt;


    @Override
    @Transactional
    public AccountEntity create(AccountEntity accountEntity, boolean doVerification) throws ApplicationException {
        if (accountEntity == null || accountEntity.getEmail() == null || accountEntity.getEmail().isEmpty()) {
            throw new InvalidRequestException("Invalid data");
        }

        AccountEntity savedEntity = getRepository().getAccountDao().getByEmail(accountEntity.getEmail());

        if (doVerification && savedEntity != null) {
            if (savedEntity.getStatus() == AccountStatus.CREATED ||
                    savedEntity.getStatus() == AccountStatus.ON_VERIFICATION) {

                String token = sendActivationCode(savedEntity);

                getEventsService().addEvent(accountEntity.getEmail(), EventType.DUPLICATE_REGISTRATION,
                        String.format("Activation code: %s", token));

                throw new AccountAlreadyExistsException("You already sent registration request, " +
                        "but didn't confirm activation code. Activation code has been resent one more time");
            } else {
                throw new AccountAlreadyExistsException("Account already exists");
            }
        }

        savedEntity = saveNewAccount(accountEntity);

        if (doVerification) {
            sendActivationCode(savedEntity);
            getEventsService().addEvent(accountEntity.getEmail(), EventType.REGISTRATION);
            getMetricsService().event(new Account(accountEntity), MetricType.REGISTRATION);
        }
        return accountEntity;
    }

    private String sendActivationCode(AccountEntity savedEntity) throws ApplicationException {
        /**
         * Generate activation code
         */
        String token = generateJWTToken(savedEntity, JWTTokenType.ACTIVATION);

        /**
         * Send code
         */
        String link = appConfiguration.getUrl() + "/reg-" + token;

        NotificationEmail notification = NotificationBuilder.get().email()
                .subject("Activation Code")
                .title("Activate your account")
                .simple(savedEntity)
                .message("To confirm registration, please follow this <a href='" + link + "'>link</a>")
                .build();

        getNotificationService().notification(notification);

        /**
         * Set status
         */
        savedEntity.setStatus(AccountStatus.ON_VERIFICATION);
        getRepository().getAccountDao().update(savedEntity);

        return token;
    }

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
        return email != null && appConfiguration.getAdministrators().contains(email.toLowerCase());
    }

    @Override
    @Transactional
    public Account update(String uid, Account newData) {
        Account account = readCachedCopy(uid);

        AccountEntity accountEntity = getRepository().getAccountDao().getByUid(account.getUid());
        accountEntity.setUserName(newData.getUserName());
        accountEntity.setOccupation(newData.getOccupation());
        accountEntity.setCompany(newData.getCompany());
        accountEntity.setAbout(newData.getAbout());

        AccountEntity entity = getRepository().getAccountDao().update(accountEntity);
        return updateAccountInCache(entity.getUid());
    }

    public String getSaltByAccount(AccountEntity accountEntity) {
        return (accountEntity.getId()
                + accountEntity.getUid()).substring(0, 8);
    }

    @Override
    @Transactional
    public ActivationResult activate(ActivationAccount activation) throws ApplicationException {
        String activationCode = activation.getCode();

        if (activationCode == null || activationCode.isEmpty()) {
            throw new EntityNotFoundException("Please specify activation code");
        }

        JWTTokenType tokenType = JWTTokenType.ACTIVATION;

        AccountEntity account = lookupAccountByToken(activationCode, tokenType);

        if (account.getStatus() != AccountStatus.ON_VERIFICATION) {
            if (account.getStatus() == AccountStatus.VERIFIED) {
                throw new ApplicationException("Account already activated");
            } else if (account.getStatus() == AccountStatus.BLOCKED) {
                throw new ApplicationException("Account is blocked");
            } else {
                throw new ApplicationException("Can't process your request");
            }
        }

        PasswordEntity newPassword = new PasswordEntity();
        String salt = getSaltByAccount(account);
        newPassword.setPasswordHash(DigestUtils.shaHex(salt + activation.getPassword()));
        account.setUserName(activation.getUserName());
        account.setPassword(newPassword);
        account.setStatus(AccountStatus.VERIFIED);
        getRepository().getAccountDao().update(account);

        /**
         * Open session
         */

        getEventsService().addEvent(account.getEmail(), EventType.ACTIVATION);
        getMetricsService().event(new Account(account), MetricType.ACTIVATION);

        /**
         * Return result
         */
        ActivationResult result = new ActivationResult();
        result.setAccount(account);
        return result;
    }

    private AccountEntity lookupAccountByToken(final String activationCode, final JWTTokenType tokenType)
            throws ApplicationException {

        JWT.Data data;
        AccountEntity account;
        try {
            data = jwt.extractPayload(activationCode);
            account = getRepository().getAccountDao().getByEmail(data.getPrincipal());

        } catch (JoseException | InvalidJwtException | MalformedClaimException e) {
            throw new ApplicationException(e.getMessage());
        }

        if (account == null || !("" + tokenType).equals(data.getAction())) {
            throw new ApplicationException("Token is not valid");
        }

        return account;
    }

    @Override
    @Transactional
    public void removeSelf(Account account) throws ApplicationException {
        AccountEntity accountEntity = getRepository().getAccountDao().getByUid(account.getUid());
        removeDependsAndAccount(accountEntity);
        getEventsService().addEvent(accountEntity.getEmail(), EventType.SELF_REMOVAL);
    }

    @Override
    @Transactional
    public void remove(String uid) throws ApplicationException {
        remove(getRepository().getAccountDao().getByUid(uid));
    }

    @Override
    @Transactional
    public void remove(AccountEntity accountEntity) throws ApplicationException {
        removeDependsAndAccount(accountEntity);
        getEventsService().addEvent(accountEntity.getEmail(), EventType.DELETE_USER);
    }

    @Override
    @Transactional
    public void removeAllAccounts(Account account) throws ApplicationException {
        getEventsService().addEvent(account.getEmail(), EventType.DELETE_ALL_USERS);

        List<AccountEntity> accounts = getRepository().getAccountDao().readAll();
        for (AccountEntity a : accounts) {
            removeDependsAndAccount(a);
        }
    }

    @Override
    @Transactional
    public void forgotPassword(String email) throws ApplicationException {
        AccountEntity accountEntity = getRepository().getAccountDao().getByEmail(email);
        if (accountEntity != null) {
            String token = generateJWTToken(accountEntity, JWTTokenType.FORGOT_PASSWORD);

            Notification notification = NotificationBuilder.get().email()
                    .subject("Forgot password")
                    .title("Forgot password")
                    .simple(email)
                    .message("Please follow this <a href='" + appConfiguration.getUrl()
                            + "/forgot-" + token + "'>link</a> to change password")
                    .build();

            getNotificationService().notification(notification);
        } else {
            try {
                Thread.sleep((long) (1000 + Math.random() * 3000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String generateJWTToken(AccountEntity accountEntity, JWTTokenType tokenType) throws ApplicationException {
        String token;
        try {
            token = jwt.buildToken(new JWT.Data(
                    accountEntity.getEmail(), "" + tokenType, ""));
        } catch (JoseException e) {
            throw new ApplicationException(e.getMessage());
        }
        return token;
    }


    @Override
    @Transactional
    public void newPassword(String code, String password)
            throws ApplicationException {
        AccountEntity accountEntity = lookupAccountByToken(code, JWTTokenType.FORGOT_PASSWORD);

        PasswordEntity newPassword = new PasswordEntity();
        newPassword.setPasswordHash(DigestUtils.shaHex(getSaltByAccount(accountEntity) + password));
        accountEntity.setPassword(newPassword);

        getRepository().getAccountDao().update(accountEntity);
    }

    @Transactional
    protected AccountEntity getSelfRemovedUser() {
        AccountEntity accountEntity = getRepository().getAccountDao().getByUid("");
        if (accountEntity == null) {
            accountEntity = new AccountEntity();
            accountEntity.setUid("");
            accountEntity.setUserName("Deleted profile");
            accountEntity = getRepository().getAccountDao().create(accountEntity);
        }
        return accountEntity;
    }

    @Transactional
    private void removeDependsAndAccount(AccountEntity accountEntity) throws ApplicationException {
        if (accountEntity.getUid() != null && accountEntity.getUid().length() > 0) {
            List<VerificationCodeEntity> verificationCodeEntities = getRepository()
                    .getVerificationCodeDao().getByAccountId(accountEntity.getId());
            for (VerificationCodeEntity code : verificationCodeEntities) {
                getRepository().getVerificationCodeDao().remove(code.getId());
            }

            List<OAuthTokenEntity> list2 = getRepository()
                    .getOAuthTokenDao().getByAccount(accountEntity.getId());
            if (list2 != null) {
                for (OAuthTokenEntity e : list2) {
                    getRepository().getOAuthTokenDao().remove(e.getId());
                }
            }

            List<SessionEntity> sessionEntities = getRepository().getSessionDao()
                    .byAccountId(accountEntity.getId());
            if (sessionEntities != null) {
                for (SessionEntity sessionEntity : sessionEntities) {
                    getRepository().getSessionDao().remove(sessionEntity.getId());
                }
            }

            getRepository().getAccountDao().remove(accountEntity.getId());
        }

        removeAccountFromCache(accountEntity.getUid());
    }

    @Override
    public Account readExtracted(Account caller, String uid, boolean showEmail) {
        Account account = readCachedCopy(uid);

        if (caller == null || !caller.getUid().equals(uid)) {
            if (!showEmail) {
                account.setEmail(null);
            }

            account.setLinkedInConnect(null);
            account.setPoints(null);
            account.setPointsHolded(null);
            account.setPasswordIsNotDefined(null);
            account.setRole(null);

        }

        return account;
    }

    @Transactional
    @CacheResult(cacheName = "accounts")
    public Account readCached(@CacheKey String uid) {
        AccountEntity entity = getRepository().getAccountDao().getByUid(uid);
        if (entity != null) {
            return fillAccount(entity);
        }
        return null;
    }

    public Account readCachedCopy(String uid) {
        Account cached = readCached(uid);
        if (cached == null) {
            return null;
        } else {
            return cached.copy();
        }
    }

    @Override
    @Transactional
    public String readUserPicture(Account caller, String uid) {
        AccountEntity entity = getRepository().getAccountDao().getByUid(uid);

        if (caller == null || entity == null) {
            return null;
        }

        if (caller.getUid().equals(entity.getUid())) {
            return entity.getPicture();
        }
        return null;
    }

    @Override
    @Transactional
    public Account readExtracted(Account account) {
        AccountEntity accountEntity = getRepository().getAccountDao().getByUid(account.getUid());
        return fillAccount(accountEntity);
    }

    private Account fillAccount(AccountEntity entity) {
        Account account = new Account(entity);
        account.setLinkedInConnect(getOAuthService().getValidOAuthCode(entity, OAuthType.LINKEDIN) != null);
        account.setFacebookConnect(getOAuthService().getValidOAuthCode(entity, OAuthType.FACEBOOK) != null);
        account.setGoogleConnect(getOAuthService().getValidOAuthCode(entity, OAuthType.GOOGLE) != null);
        account.setGithubConnect(getOAuthService().getValidOAuthCode(entity, OAuthType.GITHUB) != null);
        return account;
    }

    public Account updateAccountInCache(String uid) {
        AccountEntity entity = getRepository().getAccountDao().getByUid(uid);
        Account account = fillAccount(entity);
        DefaultGeneratedCacheKey key =
                new DefaultGeneratedCacheKey(new Object[]{account.getUid()});
        Caching.getCachingProvider().getCacheManager()
                .getCache("accounts").put(key, account);

        return account;
    }

    public void removeAccountFromCache(String uid) {
        DefaultGeneratedCacheKey key =
                new DefaultGeneratedCacheKey(new Object[]{uid});

        Caching.getCachingProvider().getCacheManager()
                .getCache("accounts").remove(key);
    }

    public boolean safe(Boolean value, boolean def) {
        if (value != null) {
            return value;
        }
        return def;
    }

    @Override
    @Transactional
    public Token signOn(String email, String password, String ip) throws ApplicationException {
        AccountEntity account = getRepository().getAccountDao().getByEmail(email);

        if (account == null || account.getPassword() == null) {
            signOnFail(email);
        }

        String salt = getSaltByAccount(account);
        String hash = DigestUtils.shaHex(salt + password);
        PasswordEntity passwordEntity = account.getPassword();

        if (passwordEntity.getPasswordHash().equals(hash)) {
            return jwt.generate(account.getUid(), ip);
        }
        throw new UnauthorizedException("Email or password is not valid");
    }

    @Override
    @Transactional
    public void deleteUnusedSessions() throws ApplicationException {
        getRepository().getSessionDao().removeUnused();
    }

    private SessionEntity signOnFail(String email) throws EntityNotFoundException {
        getEventsService().addEvent(email, EventType.SIGNON_FAIL);
        throw new EntityNotFoundException("Email or password is not valid");
    }

    @Override
    @Transactional
    public AccountsContainer getAccounts(Long count, Long page, Map<String, String> sort) {
        Set<Account> result = new LinkedHashSet<>();
        List<String> uids = getRepository().getAccountDao().read(count, page, sort);

        if (uids != null) {
            result.addAll(uids.stream()
                    .map(this::readCachedCopy)
                    .collect(Collectors.toList()));
        }

        AccountsContainer container = new AccountsContainer();
        container.setResult(result);
        container.setTotal(getRepository().getAccountDao().count());
        return container;
    }


    public ApplicationRepository getRepository() {
        return repository;
    }

    public void setRepository(ApplicationRepository repository) {
        this.repository = repository;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public EventsService getEventsService() {
        return eventsService;
    }

    public void setEventsService(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    public MetricsService getMetricsService() {
        return metricsService;
    }

    public void setMetricsService(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    public OAuthService getOAuthService() {
        return oAuthService;
    }

    public void setoAuthService(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }
}
