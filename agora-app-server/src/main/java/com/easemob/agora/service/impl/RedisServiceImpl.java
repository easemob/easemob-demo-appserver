//package com.easemob.agora.service.impl;
//
//import com.easemob.agora.config.redis.RedisKeyConstants;
//import com.easemob.agora.service.RedisService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//
//@Slf4j
//@Service
//public class RedisServiceImpl implements RedisService {
//
//    @Autowired
//    @Qualifier("channelRedis")
//    private JedisPool channelRedis;
//
//    @Value("${spring.redis.get.token.limit.expireTime.seconds}")
//    private int getTokenLimitExpireTime;
//
//    @Override
//    public Long appUserGetCountOfToken(String userAccount) {
//        try (Jedis jedis = channelRedis.getResource()) {
//            Long count;
//            if (jedis.get(String.format(RedisKeyConstants.GET_USER_TOKEN_COUNT, userAccount)) == null) {
//                count = jedis.incr(String.format(RedisKeyConstants.GET_USER_TOKEN_COUNT, userAccount));
//                jedis.expire(String.format(RedisKeyConstants.GET_USER_TOKEN_COUNT, userAccount), getTokenLimitExpireTime);
//            } else {
//                count = jedis.incr(String.format(RedisKeyConstants.GET_USER_TOKEN_COUNT, userAccount));
//            }
//            return count;
//        } catch (Exception e) {
//            return 0L;
//        }
//    }
//
//}
