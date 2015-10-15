package com.mlx.accounts.rest;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.mlx.accounts.exception.ApplicationException;
import com.mlx.accounts.model.Account;
import com.mlx.accounts.model.OAuthData;
import com.mlx.accounts.model.entity.AccountEntity;
import com.mlx.accounts.model.entity.AccountStatus;
import com.mlx.accounts.model.entity.PasswordEntity;
import com.mlx.accounts.model.entity.UserRoles;
import com.mlx.accounts.repository.ApplicationRepository;
import com.mlx.accounts.service.AccountService;
import com.mlx.accounts.service.OAuthService;
import io.dropwizard.auth.Auth;
import org.apache.commons.codec.digest.DigestUtils;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

/**
 * 9/9/14.
 */
@Singleton
@Path("/api/magic")
@PermitAll
public class MagicResource {
    public static final String TEST_PASSWORD = "password";

    @Inject
    AccountService accountService;

    @Inject
    OAuthService oauthService;

    @Inject
    ApplicationRepository repository;


    private void register10(Long amount) throws ApplicationException {
        for (int k = 0; k < amount; k++) {
            String email = "mlx" + k + "@grr.la";
            AccountEntity accountEntity = getRepository().getAccountDao().getByEmail(email);
            if (accountEntity == null) {
                accountEntity = new AccountEntity();
                accountEntity.setUid(UUID.randomUUID().toString());
                accountEntity.setEmail(email);
                accountEntity = getAccountService().create(accountEntity, false);
            }

            System.out.println("Create " + accountEntity.getEmail());

            String[] FIRST_NAMES = new String[]{"Alex", "Anna", "Bryan", "Ben", "Eugene", "Daniil",
                    "Tanya", "Mike", "Pavel", "Din", "Sergey", "Slava", "Tom", "Peter", "Scott"};
            String[] LAST_NAMES = new String[]{"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis",
                    "Miller", "Wilson", "Moore", "Taylor", "Anderson", "Thomas", "Jackson", "White"};

            //int num = (int) (Math.random() * 10000);


            accountEntity.setStatus(AccountStatus.VERIFIED);

            PasswordEntity newPassword = new PasswordEntity();
            newPassword.setPasswordHash(DigestUtils.shaHex(
                    getAccountService().getSaltByAccount(accountEntity) + TEST_PASSWORD));
            accountEntity.setPassword(newPassword);

            getRepository().getAccountDao().update(accountEntity);
            accountService.updateAccountInCache(accountEntity.getUid());

            /**
             * Setup account
             */
            OAuthData oauthData = new OAuthData();
            oauthData.setFirstName(FIRST_NAMES[((int) (Math.random() * FIRST_NAMES.length))]);
            oauthData.setLastName(LAST_NAMES[((int) (Math.random() * LAST_NAMES.length))] + " " + k);
            oauthData.setSummary(rndString(wikiArticle(), 2000));
            getOauthService().updateProfile(accountEntity, oauthData);
        }
    }

    private static String wikiArticle() {
        return UUID.randomUUID().toString();
    }

    private static String rndString(String bodyContentText, long max) {
        String rndPart;
        int l = bodyContentText.length();
        if (l < 300) {
            rndPart = bodyContentText;
        } else {
            int b = 1 + (int) (Math.random() * (l / 2));
            int e = 1 + (int) (Math.random() * (l / 2));
            if (e > max) {
                e = 1 + (int) (Math.random() * max);
            }
            rndPart = bodyContentText.substring(b, b + e).trim();
            if (rndPart.contains(" ")) {
                rndPart = rndPart.substring(rndPart.indexOf(' ') + 1);
            }
        }
        return rndPart;
    }


    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Transactional
    @Path("/register100")
    @RolesAllowed(UserRoles.ADMINISTRATOR)
    public Response magicRegister100(
            @Auth Account account)
            throws ApplicationException {
        register10(300l);
        return Response
                .status(Response.Status.OK)
                .build();
    }

    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Transactional
    @Path("/delete100«")
    @RolesAllowed(UserRoles.ADMINISTRATOR)
    public Response magicDelete100(
            @Auth Account account)
            throws ApplicationException {
        for (int k = 0; k < 300; k++) {
            String email = "mlx" + k + "@grr.la";
            List<AccountEntity> accountEntities = getRepository().getAccountDao().getAllByEmail(email);
            if (accountEntities != null && accountEntities.size() > 0) {
                for (AccountEntity accountEntity : accountEntities) {
                    System.out.println("Delete " + accountEntity.getEmail());
                    getAccountService().remove(accountEntity);
                }
            }
        }
        return Response
                .status(Response.Status.OK)
                .build();
    }


    public AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public ApplicationRepository getRepository() {
        return repository;
    }

    public void setRepository(ApplicationRepository repository) {
        this.repository = repository;
    }

    public OAuthService getOauthService() {
        return oauthService;
    }

    public void setOauthService(OAuthService oauthService) {
        this.oauthService = oauthService;
    }
}
