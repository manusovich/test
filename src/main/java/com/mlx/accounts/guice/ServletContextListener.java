package com.mlx.accounts.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class ServletContextListener extends GuiceServletContextListener {
    @Override
    public Injector getInjector() {
        return Guice.createInjector();
    }
}