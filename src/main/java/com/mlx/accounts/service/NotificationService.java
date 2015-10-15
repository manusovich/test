package com.mlx.accounts.service;

import com.mlx.accounts.exception.ApplicationException;
import com.mlx.accounts.model.Notification;

/**
 * <p>
 * 9/8/14.
 */
public interface NotificationService {

//    void notification(AccountEntity sender, AccountEntity recipient, String subject,
//                      NotificationTemplate template, HashMap<String, Object> params) throws TokenExpiredException;

    void notification(Notification notification) throws ApplicationException;

//    void notification(AccountEntity sender,
//                      AccountEntity recipient,
//                      String subject,
//                      NotificationTemplate template,
//                      HashMap<String, Object> params,
//                      boolean addSenderToCc) throws TokenExpiredException;

//    void notification(AccountEntity accountEntity, String subject,
//                      String title, String text) throws TokenExpiredException;

//    void notification(MailAddress recipient,
//                      String subject,
//                      String title,
//                      String text) throws TokenExpiredException;

    void systemAlert(String text);
}
