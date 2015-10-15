package com.mlx.accounts.service.impl;

import com.mixpanel.mixpanelapi.ClientDelivery;
import com.mixpanel.mixpanelapi.MessageBuilder;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import com.mlx.accounts.AppConfiguration;
import com.mlx.accounts.model.Account;
import com.mlx.accounts.model.MetricType;
import com.mlx.accounts.service.MetricsService;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

/**
 * <p>
 * 9/8/14.
 */
@Singleton
public class MetricsServiceImpl implements MetricsService {
    @Inject
    private AppConfiguration appConfiguration;

    private MessageBuilder messageBuilder;

    private MixpanelAPI mixpanel = new MixpanelAPI();

    @Override
    public void event(Account account, MetricType metric) {
        event(account, metric, null, null);
    }

    public void event(final Account account,
                      final MetricType metric,
                      final Map<String, String> userParameters,
                      final Map<String, String> userModifiers) {

        if (account == null) {
            return;
        }

        if (account.getEmail() != null && account.getEmail().contains("@grr.la")) {
            return;
        }

        new Thread(() -> {
            try {
                ClientDelivery delivery = new ClientDelivery();

                if (metric == MetricType.SIGNON || metric == MetricType.SIGNON_LINKEDIN) {
                    JSONObject properties = new JSONObject();
                    JSONObject modifiers = new JSONObject();
                    properties.put("$email", account.getEmail());
                    properties.put("$name", account.getUserName());

                    if (userParameters != null && userParameters.size() > 0) {
                        for (String key : userParameters.keySet()) {
                            properties.put(key, userParameters.get(key));
                        }
                    }
                    if (userModifiers != null && userModifiers.size() > 0) {
                        for (String key : userModifiers.keySet()) {
                            modifiers.put(key, userModifiers.get(key));
                        }
                    }

                    JSONObject userInformation = builder().set(
                            "" + account.getUid(), properties, modifiers);
                    delivery.addMessage(userInformation);
                }

                JSONObject event = builder()
                        .event("" + account.getUid(), "" + metric, null);
                delivery.addMessage(event);

                mixpanel.deliver(delivery);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).run();
    }

    private MessageBuilder builder() {
        if (messageBuilder == null) {
            messageBuilder = new MessageBuilder(
                    appConfiguration.getMixPanelConfiguration().getToken());
        }
        return messageBuilder;
    }


    public AppConfiguration getAppConfiguration() {
        return appConfiguration;
    }

    public void setAppConfiguration(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }
}
