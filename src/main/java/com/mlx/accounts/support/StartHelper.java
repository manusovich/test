package com.mlx.accounts.support;

import com.mlx.accounts.AppConfiguration;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;

import javax.validation.Validation;
import java.io.File;
import java.util.Properties;

/**
 * 8/29/15.
 */
public class StartHelper {
    public static final String JPA_UNIT = "d1";
    private static String configFilename;

    public static void setConfigFilename(String configFilename) {
        StartHelper.configFilename = configFilename;
    }

    public static String getConfigFilename() {
        return configFilename;
    }


    public static Properties createPropertiesFromConfiguration(AppConfiguration localConfiguration) {

        AppConfiguration.DatabaseConfiguration databaseConfiguration =
                localConfiguration.getDatabaseConfiguration();

        Properties properties = new Properties();
        properties.setProperty("javax.persistence.jdbc.driver",
                databaseConfiguration.getDriverClass());
        properties.setProperty("javax.persistence.jdbc.url",
                databaseConfiguration.getUrl());
        properties.setProperty("javax.persistence.jdbc.user",
                databaseConfiguration.getUser());
        properties.setProperty("javax.persistence.jdbc.password",
                databaseConfiguration.getPassword());

        properties.putAll(databaseConfiguration.getProperties());

        return properties;
    }

    public static AppConfiguration createConfiguration(String configFilename) {
        ConfigurationFactory<AppConfiguration> factory =
                new ConfigurationFactory<>(
                        AppConfiguration.class,
                        Validation.buildDefaultValidatorFactory().getValidator(),
                        Jackson.newObjectMapper(),
                        ""
                );

        AppConfiguration configuration;
        try {
            configuration = factory.build(new File(configFilename));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return configuration;
    }
}
