package com.easemob.discord.config;

import com.easemob.im.server.EMProperties;
import com.easemob.im.server.EMService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("application")
public class AppConfig {
    private String baseUri;

    private String appkey;

    private String clientId;

    private String clientSecret;

    @Bean
    public EMService service() {

        EMProperties properties = EMProperties.builder()
                .setAppkey(this.appkey)
                .setClientId(this.clientId)
                .setClientSecret(this.clientSecret)
                .turnOffUserNameValidation()
                .build();

        return new EMService(properties);
    }

}
