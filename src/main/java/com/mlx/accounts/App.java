package com.mlx.accounts;

import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Verifier;
import com.github.toastshaman.dropwizard.auth.jwt.parser.DefaultJsonWebTokenParser;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.hubspot.dropwizard.guice.GuiceBundle;
import com.mlx.accounts.auth.JWTAuthFactory;
import com.mlx.accounts.auth.JWTAuthenticator;
import com.mlx.accounts.guice.AppModule;
import com.mlx.accounts.guice.ServletContextListener;
import com.mlx.accounts.health.DBHealthCheck;
import com.mlx.accounts.model.Account;
import com.mlx.accounts.support.StartHelper;
import com.xeiam.dropwizard.sundial.SundialBundle;
import com.xeiam.dropwizard.sundial.SundialConfiguration;
import com.yunspace.dropwizard.xml.XmlBundle;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.jsr107.ri.annotations.guice.module.CacheAnnotationsModule;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
import java.util.Properties;

public class App extends Application<AppConfiguration> {
    public static GuiceBundle<AppConfiguration> guice;

    public static void main(String[] args) throws Exception {
        for (String arg : args) {
            if (arg.endsWith(".yml")) {
                StartHelper.setConfigFilename(arg);
            }
        }
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        AppConfiguration configuration = StartHelper.createConfiguration(StartHelper.getConfigFilename());
        Properties jpaProperties = StartHelper.createPropertiesFromConfiguration(configuration);

        JpaPersistModule jpaPersistModule = new JpaPersistModule(StartHelper.JPA_UNIT);
        jpaPersistModule.properties(jpaProperties);

        AppModule appModule = new AppModule();
        appModule.setConfiguration(configuration);
        guice = GuiceBundle.<AppConfiguration>newBuilder()
                .addModule(jpaPersistModule)
                .addModule(appModule)
                .addModule(new CacheAnnotationsModule())
                .enableAutoConfig(getClass().getPackage().getName())
                .build();


//        bootstrap.addBundle(new MigrationsBundle<AppConfiguration>() {
//            @Override
//            public DataSourceFactory getDataSourceFactory(AppConfiguration configuration) {
//                DataSourceFactory dataSourceFactory = new DataSourceFactory();
//                dataSourceFactory.setDriverClass(configuration.getDatabaseConfiguration().getDriverClass());
//                dataSourceFactory.setUrl(configuration.getDatabaseConfiguration().getUrl());
//                dataSourceFactory.setUser(configuration.getDatabaseConfiguration().getUser());
//                dataSourceFactory.setPassword(configuration.getDatabaseConfiguration().getPassword());
////                dataSourceFactory.setProperties(configuration.getDatabaseConfiguration().getProperties());
//                return dataSourceFactory;
//            }
//        });

        bootstrap.addBundle(new SundialBundle<AppConfiguration>() {
            @Override
            public SundialConfiguration getSundialConfiguration(AppConfiguration configuration) {
                return configuration.getSundialConfiguration();
            }
        });

        bootstrap.addBundle(guice);
        bootstrap.addBundle(new ViewBundle<>());
        bootstrap.addBundle(new XmlBundle());
    }

    @Override
    public void run(AppConfiguration conf, Environment env) throws Exception {
        env.servlets().addFilter("persistFilter",
                guice.getInjector().getInstance(PersistFilter.class))
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        JWTAuthenticator JWTAuthenticator =
                guice.getInjector().getInstance(JWTAuthenticator.class);
        String authenticationSecret = conf.getJwtConfiguration().getAuthenticationSecret();
        JWTAuthFactory<Account> jwtAuthFactory = new JWTAuthFactory<>(
                JWTAuthenticator,
                "realm",
                Account.class,
                new HmacSHA512Verifier(authenticationSecret.getBytes()),
                new DefaultJsonWebTokenParser());
        env.jersey().register(AuthFactory.binder(jwtAuthFactory));

        FilterRegistration.Dynamic filter = env.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filter.setInitParameter(CrossOriginFilter.EXPOSED_HEADERS_PARAM,
                "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,Location");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
                "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,Location");
        filter.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");

        env.healthChecks().register("DB", guice.getInjector().getInstance(DBHealthCheck.class));

        env.servlets().addServletListeners(new ServletContextListener());
    }

    public static GuiceBundle<AppConfiguration> getGuice() {
        return guice;
    }
}
