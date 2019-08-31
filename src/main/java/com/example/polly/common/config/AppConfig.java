package com.example.polly.common.config;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("com.example.awspollytest.awspollytest.**")
//@PropertySource("classpath:database.properties")
@EnableTransactionManagement
public class AppConfig {
    private static final String APPLICATION_CONFIG_PATH = "application.properties";

    @Bean
    public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setLocations(new ClassPathResource(APPLICATION_CONFIG_PATH));
        return configurer;
    }

    @Bean
    public EnvironmentStringPBEConfig encryptConfig() {
        EnvironmentStringPBEConfig environmentStringPBEConfig = new EnvironmentStringPBEConfig();
        environmentStringPBEConfig.setAlgorithm("PBEWithMD5AndDES");
        environmentStringPBEConfig.setPasswordEnvName("APP_ENCRYPTION_PASSWORD");
        return environmentStringPBEConfig;
    }


    @Bean
    public PooledPBEStringEncryptor encryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(encryptConfig());
        encryptor.setPassword("AMATHON-POLLY-SERVICE");
        encryptor.setPoolSize(1);
        return encryptor;
    }

    @Bean(name = "envProp")
    public PropertiesFactoryBean envProp() {
        PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setLocation(new ClassPathResource(APPLICATION_CONFIG_PATH));
        return bean;
    }
}
