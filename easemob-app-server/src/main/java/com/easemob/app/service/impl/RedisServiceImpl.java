package com.easemob.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.easemob.app.config.redis.RedisKeyConstants;
import com.easemob.app.model.ChatGptMessage;
import com.easemob.app.service.RedisService;
import com.easemob.app.utils.RandomUidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    @Qualifier("channelRedis")
    private StringRedisTemplate redisTemplate;

    @Value("${application.agora.appId}")
    private String agoraAppId;

    @Value("${spring.redis.channel.expireTime}")
    private long expireTime;

    @Value("${send.message.to.chatgpt.day.count.limit}")
    private long sendMessageToChatGptDayCount;

    @Value("${chat.group.messages.context.count.limit:5}")
    private Long contextCountLimit;

    @Override
    public void saveAgoraChannelInfo(boolean isRandomUid, String channelName, String uid) {
        Long result;
        String redisKey = String.format(RedisKeyConstants.AGORA_CHANNEL_INFO, agoraAppId, channelName);

        while (true) {
            if (isRandomUid) {
                try {

                    result = redisTemplate.opsForSet().add(redisKey, uid);

                    if (result != null) {
                        if (result == 1) {
                            redisTemplate.expire(redisKey, 600, TimeUnit.SECONDS);
                            break;
                        } else {
                            uid = RandomUidUtil.getUid();
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
            String redisKey = String.format(RedisKeyConstants.AGORA_UID, agoraAppId, uid);
            redisTemplate.opsForValue().set(redisKey, easemobUserId, Duration.ofSeconds(expireTime));
        } catch (Exception e) {
            log.error("save uid mapper failed. Message - {}", e.getMessage(), e);
        }
    }

    @Override
    public Set<String> getAgoraChannelInfo(String channelName) {
        Set<String> channelInfo = null;

        try {
            String rediskey = String.format(RedisKeyConstants.AGORA_CHANNEL_INFO, agoraAppId, channelName);

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
            String redisKey = String.format(RedisKeyConstants.AGORA_UID, agoraAppId, uid);
            easemobUserId = redisTemplate.opsForValue().get(redisKey);
        } catch (Exception e) {
            log.error("get uid mapper failed. Message - {}", e.getMessage(), e);
        }

        if (StringUtils.isBlank(easemobUserId)) {
            return "";
        }

        return easemobUserId;
    }

    @Override public void decrNumberOfSendMessageToChatGpt(String appKey, String username) {
        String redisKey = String.format(RedisKeyConstants.SEND_MESSAGE_TO_CHATGPT_COUNT_LIMIT_DAY,
                appKey.toLowerCase(), username.toLowerCase());

        try {
            redisTemplate.opsForValue().decrement(redisKey);
        } catch (Exception e) {
            log.error(
                    "redis | decr send message to chatGpt fail. appKey : {}, username: {}, error : {}",
                    appKey, username, e.getMessage());
        }
    }

    @Override public boolean checkIfSendMessageToChatGptLimit(String appKey, String username) {
        String redisKey = String.format(RedisKeyConstants.SEND_MESSAGE_TO_CHATGPT_COUNT_LIMIT_DAY,
                appKey.toLowerCase(), username.toLowerCase());

        try {
            String dayLimit = redisTemplate.opsForValue().get(redisKey);
            if (dayLimit == null) {
                redisTemplate.opsForValue().set(redisKey, String.valueOf(sendMessageToChatGptDayCount));
                redisTemplate.expire(redisKey, expireTime, TimeUnit.SECONDS);
                return false;
            } else {
                return Integer.parseInt(dayLimit) == 0;
            }
        } catch (Exception e) {
            log.error(
                    "redis | check send message to chatGpt fail. appKey : {}, username: {}, error : {}",
                    appKey, username, e.getMessage());
            return true;
        }
    }

    @Override public void addGroupMessageToRedis(String appkey, String groupId, ChatGptMessage message) {
        if (getGroupContextMessagesCount(appkey, groupId) == contextCountLimit) {
            popGroupContextMessage(appkey, groupId);
        }

        pushGroupContextMessage(appkey, groupId, JSONObject.toJSONString(message));
    }

    @Override public List<String> getGroupContextMessages(String appkey, String groupId) {
        String redisKey = String.format(RedisKeyConstants.CHAT_GROUP_MESSAGES_CONTENT_RECORD,
                appkey.toLowerCase(), groupId.toLowerCase());

        try {
            return redisTemplate.opsForList().range(redisKey, 0, -1);
        } catch (Exception e) {
            log.error("redis | get context messages fail. appkey : {}, groupId : {}, error : {}",
                    appkey, groupId, e.getMessage());
            return null;
        }
    }

    @Override public Long getGroupContextMessagesCount(String appkey, String groupId) {
        String redisKey = String.format(RedisKeyConstants.CHAT_GROUP_MESSAGES_CONTENT_RECORD,
                appkey.toLowerCase(), groupId.toLowerCase());

        try {
            Long count = redisTemplate.opsForList().size(redisKey);
            if (count == null) {
                return 0L;
            }

            return count;
        } catch (Exception e) {
            log.error("redis | get context messages count fail. appkey : {}, groupId : {}, error : {}",
                    appkey, groupId, e.getMessage());
            return 0L;
        }
    }

    private void pushGroupContextMessage(String appkey, String groupId, String messageContent) {
        String redisKey = String.format(RedisKeyConstants.CHAT_GROUP_MESSAGES_CONTENT_RECORD,
                appkey.toLowerCase(), groupId.toLowerCase());

        try {
            redisTemplate.opsForList().leftPush(redisKey, messageContent);
        } catch (Exception e) {
            log.error("redis | push context message fail. appkey : {}, groupId : {}, error : {}",
                    appkey, groupId, e.getMessage());
        }
    }

    private void popGroupContextMessage(String appkey, String groupId) {
        String redisKey = String.format(RedisKeyConstants.CHAT_GROUP_MESSAGES_CONTENT_RECORD,
                appkey.toLowerCase(), groupId.toLowerCase());

        try {
            redisTemplate.opsForList().rightPop(redisKey);
        } catch (Exception e) {
            log.error("redis | pop context message fail. appkey : {}, groupId : {}, error : {}",
                    appkey, groupId, e.getMessage());
        }
    }
}
