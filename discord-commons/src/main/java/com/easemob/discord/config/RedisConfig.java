package com.easemob.discord.config;

import com.easemob.discord.util.RedisUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@EnableConfigurationProperties(RedisConfigProperties.class)
public class RedisConfig {

    @Bean(name = "entityRedisTemplate")
    public StringRedisTemplate callbackFromRedisTemplate(
            RedisConfigProperties redisConfigProperties) {
        return getStringRedisTemplate(redisConfigProperties.getEntity());
    }

    private StringRedisTemplate getStringRedisTemplate(
            RedisConfigProperties.Property pool) {
        RedisConnectionFactory redisConnectionFactory = connectionFactory(pool);
        return new StringRedisTemplate(redisConnectionFactory);
    }

    private RedisConnectionFactory connectionFactory(
            RedisConfigProperties.Property pool) {
        return RedisUtils.connectionFactory(pool);
    }
}
