package com.easemob.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Data
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationConfig {
    private String baseHttpUri;
    private String appkey;
    private String clientId;
    private String clientSecret;

    @Bean(name = "restTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
