package com.mlx.accounts.service;

import com.mlx.accounts.exception.ApplicationException;
import com.mlx.accounts.model.Account;
import com.mlx.accounts.model.ActivationAccount;
import com.mlx.accounts.model.Token;
import com.mlx.accounts.model.container.AccountsContainer;
import com.mlx.accounts.model.entity.AccountEntity;
import com.mlx.accounts.model.entity.ActivationResult;

import java.util.Map;

/**
 * 9/8/14.
 */
public interface AccountService {
    AccountEntity create(final AccountEntity accountEntity, boolean doVerification) throws ApplicationException;

    ActivationResult activate(ActivationAccount activation) throws ApplicationException;

    Account update(String uid, Account account);

    String getSaltByAccount(AccountEntity accountEntity);

    void removeSelf(Account account) throws ApplicationException;

    void remove(String uid) throws ApplicationException;

    void remove(AccountEntity accountEntity) throws ApplicationException;

    Account readCached(String uid);

    Account readExtracted(Account caller, String uid, boolean showEmail);

    String readUserPicture(Account caller, String uid);

    Account readExtracted(Account account);

    Account updateAccountInCache(String uid);

    Token signOn(String email, String password, String ip) throws ApplicationException;

    void deleteUnusedSessions() throws ApplicationException;

    void removeAllAccounts(Account account) throws ApplicationException;

    void forgotPassword(String email) throws ApplicationException;

    void newPassword(String code, String password) throws ApplicationException;

    AccountsContainer getAccounts(Long count, Long page, Map<String, String> sort);
}
