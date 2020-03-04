package com.easemob.live.server.rest;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URLConnection;

/**
 * @author shenchong@easemob.com 2020/3/3
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "easemob.live.rest")
public class RestProperties {

    /**
     * 环信Rest base url, e.g. http://a1.easemob.com
     */
    private String baseUrl;

    /**
     * Set the underlying URLConnection's connect timeout (in milliseconds).
     * A timeout value of 0 specifies an infinite timeout.
     * <p>Default is the system's default timeout.
     * @see URLConnection#setConnectTimeout(int)
     */
    private int connectTimeout = 10000;

    /**
     * Set the underlying URLConnection's read timeout (in milliseconds).
     * A timeout value of 0 specifies an infinite timeout.
     * <p>Default is the system's default timeout.
     * @see URLConnection#setReadTimeout(int)
     */
    private int readTimeout = 10000;

    private Appkey appkey;

    @Data
    public static class Appkey {

        /**
         * 环信企业名称
         */
        private String orgName;

        /**
         * 环信App名称
         */
        private String appName;

        /**
         * 环信AppKey client id
         */
        private String clientId;

        /**
         * 环信AppKey client secret
         */
        private String clientSecret;
    }
}
