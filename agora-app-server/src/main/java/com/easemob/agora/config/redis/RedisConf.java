//package com.easemob.agora.config.redis;
//
//import com.easemob.agora.utils.RedisUtils;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.StringRedisTemplate;
//
//@Configuration
//@EnableConfigurationProperties(RedisConfigProperties.class)
//public class RedisConf {
//
//    @Bean(name = "limitRedisTemplate")
//    public StringRedisTemplate entityRedisTemplate(RedisConfigProperties redisConfigProperties) {
//        return getStringRedisTemplate(redisConfigProperties.getClean());
//    }
//
//    private StringRedisTemplate getStringRedisTemplate(RedisConfigProperties.Property pool) {
//        RedisConnectionFactory redisConnectionFactory = connectionFactory(pool);
//        return new StringRedisTemplate(redisConnectionFactory);
//    }
//
//    private RedisConnectionFactory connectionFactory(RedisConfigProperties.Property pool) {
//        return RedisUtils.connectionFactory(pool);
//    }
//
//}
