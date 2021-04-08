package com.easemob.agora.service.impl;

import com.easemob.agora.config.ApplicationConf;
import com.easemob.agora.config.redis.RedisKeyConstants;
import com.easemob.agora.service.RedisService;
import com.easemob.agora.utils.RandomUidUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    @Qualifier("channelRedis")
    private JedisPool channelRedis;

    @Autowired
    private ApplicationConf applicationConf;

    @Value("${spring.redis.channel.expireTime}")
    private int expireTime;

    @Override
    public void saveAgoraChannelInfo(boolean isRandomUid, String channelName, String uid) {
        Long result;
        try (Jedis jedis = channelRedis.getResource()) {
            while (true) {
                if (isRandomUid) {
                    result = jedis.sadd(String.format(RedisKeyConstants.AGORA_CHANNEL_INFO, applicationConf.getAgoraAppId(), channelName), uid);
                    if (result == 1) {
                        jedis.expire(String.format(RedisKeyConstants.AGORA_CHANNEL_INFO, applicationConf.getAgoraAppId(), channelName), expireTime);
                        break;
                    } else {
                        uid = RandomUidUtil.randomUid();
                    }
                } else {
                    jedis.sadd(String.format(RedisKeyConstants.AGORA_CHANNEL_INFO, applicationConf.getAgoraAppId(), channelName), uid);
                    jedis.expire(String.format(RedisKeyConstants.AGORA_CHANNEL_INFO, applicationConf.getAgoraAppId(), channelName), expireTime);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("save agora channel info failed. Message - {}", e.getMessage(), e);
        }
    }

    @Override
    public void saveUidMapper(String uid, String easemobUserId) {
        try (Jedis jedis = channelRedis.getResource()) {
            jedis.set(String.format(RedisKeyConstants.AGORA_UID, applicationConf.getAgoraAppId(), uid), easemobUserId, SetParams.setParams().ex(expireTime));

        } catch (Exception e) {
            log.error("save uid mapper failed. Message - {}", e.getMessage(), e);
        }
    }

    @Override
    public Set<String> getAgoraChannelInfo(String channelName) {
        Jedis jedis = null;
        Set<String> channelInfo = null;
        try {
            jedis = channelRedis.getResource();
            channelInfo = jedis.smembers(String.format(RedisKeyConstants.AGORA_CHANNEL_INFO, applicationConf.getAgoraAppId(), channelName));
        } catch (Exception e) {
            log.error("get agora channel info failed. Message - {}", e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        if (channelInfo.isEmpty()) {
            return Collections.emptySet();
        }
        return channelInfo;
    }

    @Override
    public String getUidMapper(String uid) {
        Jedis jedis = null;
        String easemobUserId = null;
        try {
            jedis = channelRedis.getResource();
            easemobUserId = jedis.get(String.format(RedisKeyConstants.AGORA_UID, applicationConf.getAgoraAppId(), uid));
        } catch (Exception e) {
            log.error("get uid mapper failed. Message - {}", e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        if (easemobUserId == null || easemobUserId.isEmpty()) {
            return "";
        }
        return easemobUserId;
    }
}
