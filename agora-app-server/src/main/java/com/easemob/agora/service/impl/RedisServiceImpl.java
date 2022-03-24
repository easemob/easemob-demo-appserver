package com.easemob.agora.service.impl;

import com.easemob.agora.config.ApplicationConf;
import com.easemob.agora.config.redis.RedisKeyConstants;
import com.easemob.agora.service.RedisService;
import com.easemob.agora.utils.RandomUidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    @Qualifier("channelRedis")
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ApplicationConf applicationConf;

    @Value("${spring.redis.channel.expireTime}")
    private long expireTime;

    @Override
    public void saveAgoraChannelInfo(boolean isRandomUid, String channelName, String uid) {
        Long result;
        String redisKey = String.format(RedisKeyConstants.AGORA_CHANNEL_INFO, applicationConf.getAgoraAppId(), channelName);

        while (true) {
            if (isRandomUid) {
                try {

                    result = redisTemplate.opsForSet().add(redisKey, uid);

                    if (result != null) {
                        if (result == 1) {
                            redisTemplate.expire(redisKey, 600, TimeUnit.SECONDS);
                            break;
                        } else {
                            uid = RandomUidUtil.randomUid();
                        }
                    } else {
                        log.error("result is empty. channelName : {}, uid : {}", channelName, uid);
                    }
                } catch (Exception e) {
                    log.error("save agora channel info failed - isRandomUid. Message - {}", e.getMessage(), e);
                }
            } else {
                try {
                    redisTemplate.opsForSet().add(redisKey, uid);
                    redisTemplate.expire(redisKey, 600, TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.error("save agora channel info failed. Message - {}", e.getMessage(), e);
                }
                break;
            }
        }
    }

    @Override
    public void saveUidMapper(String uid, String easemobUserId) {
        try {
            String redisKey = String.format(RedisKeyConstants.AGORA_UID, applicationConf.getAgoraAppId(), uid);
            redisTemplate.opsForValue().set(redisKey, easemobUserId, Duration.ofSeconds(expireTime));
        } catch (Exception e) {
            log.error("save uid mapper failed. Message - {}", e.getMessage(), e);
        }
    }

    @Override
    public Set<String> getAgoraChannelInfo(String channelName) {
        Set<String> channelInfo = null;

        try {
            String rediskey = String.format(RedisKeyConstants.AGORA_CHANNEL_INFO, applicationConf.getAgoraAppId(), channelName);

            channelInfo = redisTemplate.opsForSet().members(rediskey);
        } catch (Exception e) {
            log.error("get agora channel info failed. Message - {}", e.getMessage());
        }

        if (channelInfo == null) {
            return Collections.emptySet();
        }

        return channelInfo;
    }

    @Override
    public String getUidMapper(String uid) {
        String easemobUserId = null;

        try {
            String redisKey = String.format(RedisKeyConstants.AGORA_UID, applicationConf.getAgoraAppId(), uid);
            easemobUserId = redisTemplate.opsForValue().get(redisKey);
        } catch (Exception e) {
            log.error("get uid mapper failed. Message - {}", e.getMessage(), e);
        }

        if (StringUtils.isBlank(easemobUserId)) {
            return "";
        }

        return easemobUserId;
    }
}
