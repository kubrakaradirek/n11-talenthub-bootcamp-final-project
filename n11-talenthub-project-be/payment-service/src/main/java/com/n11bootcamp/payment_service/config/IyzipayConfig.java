package com.n11bootcamp.payment_service.config;

import com.iyzipay.Options;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IyzipayProperties.class)
public class IyzipayConfig {

    @Bean
    public Options iyzipayOptions(IyzipayProperties properties) {
        Options options = new Options();
        options.setApiKey(properties.getApiKey());
        options.setSecretKey(properties.getSecretKey());
        options.setBaseUrl(properties.getBaseUrl());
        return options;
    }
}
