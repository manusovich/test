package com.mlx.accounts.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mlx.accounts.AppConfiguration;
import com.mlx.accounts.service.*;
import com.mlx.accounts.service.impl.*;

import javax.inject.Named;

public class AppModule extends AbstractModule {
    private AppConfiguration configuration;

    @Override
    protected void configure() {
        bind(AppConfiguration.class).toInstance(configuration);
        bind(AccountService.class).to(AccountServiceImpl.class).in(Singleton.class);
        bind(EventsService.class).to(EventsServiceImpl.class).in(Singleton.class);
        bind(MetricsService.class).to(MetricsServiceImpl.class).in(Singleton.class);
        bind(NotificationService.class).to(NotificationServiceImpl.class).in(Singleton.class);
        bind(OAuthService.class).to(OAuthServiceImpl.class).in(Singleton.class);
        bind(StorageService.class).to(StorageServiceImpl.class).in(Singleton.class);
    }

    public void setConfiguration(AppConfiguration configuration) {
        this.configuration = configuration;
    }

    @Provides
    @Named("template")
    public String provideTemplate() {
        return "";
    }

    @Provides
    @Named("configuration")
    public AppConfiguration provideConfiguration() {
        return configuration;
    }
}
