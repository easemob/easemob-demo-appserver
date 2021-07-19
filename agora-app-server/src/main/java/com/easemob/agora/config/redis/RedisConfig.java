package com.easemob.agora.config.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class RedisConfig {

    @Bean(name = "channelRedis")
    public JedisPool channelRedisTemplate(@Autowired RedisConfigProperties properties) {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        if (properties.getPassword() != null) {
            return new JedisPool(poolConfig, properties.getHost(), properties.getPort(), properties.getTimeout(), properties.getPassword());
        }
        return new JedisPool(poolConfig, properties.getHost(), properties.getPort(), properties.getTimeout());
    }
}
