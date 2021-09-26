package com.easemob.agora.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//@Component
//@Data
//@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfigProperties {
    private Property entity;
    private Property data;
    private Property image;
    private Property sms;
    private Property config;
    private Property message;
    private Property db;
    private Property subscribe;
    private Property lock;
    private Property device;
    private Property clean;


    @Data
    public static class Property {

        //configuration
        private String type = "standalone";
        private String master = "mymaster";
        private String nodes;
        private String password = "default_password";
        private Boolean ssl = false;
        private String connectionFactory = "lettuce";

        private Integer maxRedirects = 5;
        private Integer maxAttempts = 5;
        private Integer soTimeout = 5000;

        //pool
        private Integer maxIdle;
        private Integer minIdle;
        private Integer maxTotal = 100;
        private Long maxWait = 5000L;
        private Long readTimeout = 60000L;
        private Long connectTimeout = 2000L;

        private Boolean testOnBorrow;
        private Boolean testOnCreate;
        private Boolean testOnReturn;

        private Long timeBetweenEvictionRuns;
        private Long minEvictableIdleTimeMillis;

    }

//    private String host;
//    private int port;
//    private String password;
//    private int timeout;
}
