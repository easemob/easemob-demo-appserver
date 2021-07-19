package com.easemob.agora.config;

import com.easemob.im.server.EMProperties;
import com.easemob.im.server.EMService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description:
 * author: lijian
 * date: 2021-02-01
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationConfig {
    private String baseUri;
    private String appkey;
    private String agoraCert;
    private String agoraAppId;

    @Bean
    public EMService service() {
        EMProperties properties = EMProperties.builder()
                .setRealm(EMProperties.Realm.AGORA_REALM)
                .setBaseUri(this.baseUri)
                .setAppkey(this.appkey)
                .setAppId(this.agoraAppId)
                .setAppCert(this.agoraCert)
                .build();

        return new EMService(properties);
    }
}
