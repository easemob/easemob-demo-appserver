package com.easemob.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author seekingua
 * @date 2018/12/6
 * @email zhouzd@easemob.com
 */
@Data
@Component
@ConfigurationProperties(prefix = "rest")
public class Rest2Properties {

    /**
     * auth url
     */
    private String authUrl;

    /**
     * username for authorization
     */
    private String username;

    /**
     * password for authorization
     */
    private String password;

    /**
     * client read timeout
     */
    private int readTimeout = 1000;

    /**
     * client connection timeout
     */
    private int connectTimeout = 1000;
}
