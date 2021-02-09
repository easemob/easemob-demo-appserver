package com.easemob.agora.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * description:
 * author: lijian
 * date: 2021-02-01
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationConf {

    private String orgName;
    private String appName;
    private String restServer;
    private String agoraCert;
    private String agoraAppId;
}
