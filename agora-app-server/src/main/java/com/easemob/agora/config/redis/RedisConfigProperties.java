package com.easemob.agora.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "spring.redis.channel")
public class RedisConfigProperties {
    private String host;
    private int port;
    private String password;
    private int timeout;
}
