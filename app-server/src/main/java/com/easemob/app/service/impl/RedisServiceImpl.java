package com.easemob.app.service.impl;

import com.easemob.app.config.redis.RedisKeyConstants;
import com.easemob.app.service.RedisService;
import com.easemob.app.utils.RandomUidUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    @Qualifier("channelRedis")
    private StringRedisTemplate redisTemplate;

    @Value("${application.agoraAppId}")
    private String agoraAppId;

    @Value("${spring.redis.channel.expireTime}")
    private long expireTime;

    @Value("${agora.token.expire.period.seconds}")
    private long agoraTokenExpireTime;

    @Value("${easemob.sms.code.day.count.limit}")
    private long smsCodeDayCount;

    @Value("${easemob.sms.code.hour.count.limit}")
    private long smsCodeHourCount;

    @Value("${easemob.sms.code.ip.count.limit}")
    private long smsCodeIpCount;

    @Value("${easemob.sms.code.validity.seconds}")
    private long smsCodeValidity;

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
                            uid = RandomUidUtils.getUid();
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

    @Override public void saveSmsCode(String phoneNumber, String smsCode, String resourceIp) {
        try {
            String redisKeyRecord = String.format(RedisKeyConstants.PHONE_SMS_CODE_RECORD, phoneNumber);
            String redisKeyValidity = String.format("%s%s", phoneNumber, System.currentTimeMillis());
            redisTemplate.opsForValue().set(redisKeyValidity, smsCode, 300, TimeUnit.SECONDS);

            redisTemplate.opsForHash().put(redisKeyRecord, redisKeyValidity, smsCode);

            String redisKeyMinute = String.format(RedisKeyConstants.PHONE_SMS_CODE_SEND_COUNT_LIMIT_MINUTE, phoneNumber);
            String redisKeyDay = String.format(RedisKeyConstants.PHONE_SMS_CODE_SEND_COUNT_LIMIT_DAY, phoneNumber);
            String redisKeyHour = String.format(RedisKeyConstants.PHONE_SMS_CODE_SEND_COUNT_LIMIT_HOUR, phoneNumber);
            String redisKeyIp = String.format(RedisKeyConstants.PHONE_SMS_CODE_SEND_COUNT_LIMIT_IP, resourceIp);

            redisTemplate.opsForValue().set(redisKeyMinute, smsCode, Duration.ofSeconds(smsCodeValidity));
            redisTemplate.opsForValue().decrement(redisKeyDay);
            redisTemplate.opsForValue().decrement(redisKeyHour);
            redisTemplate.opsForValue().decrement(redisKeyIp);

        } catch (Exception e) {
            log.error("save phone sms code failed v1. phoneNUmber : {}, Message - {}", phoneNumber, e.getMessage());
        }
    }

    @Override
    public void checkSmsCodeLimit(String phoneNumber, String resourceIp) {
        String redisKeyDay = String.format(RedisKeyConstants.PHONE_SMS_CODE_SEND_COUNT_LIMIT_DAY, phoneNumber);
        String redisKeyHour = String.format(RedisKeyConstants.PHONE_SMS_CODE_SEND_COUNT_LIMIT_HOUR, phoneNumber);
        String redisKeyMinute = String.format(RedisKeyConstants.PHONE_SMS_CODE_SEND_COUNT_LIMIT_MINUTE, phoneNumber);
        String redisKeyIp = String.format(RedisKeyConstants.PHONE_SMS_CODE_SEND_COUNT_LIMIT_IP, resourceIp);

        String ipLimit = redisTemplate.opsForValue().get(redisKeyIp);
        if (ipLimit == null) {
            redisTemplate.opsForValue().set(redisKeyIp, String.valueOf(smsCodeIpCount));
            redisTemplate.expire(redisKeyIp, 86400, TimeUnit.SECONDS);
        } else {
            if (Integer.parseInt(ipLimit) == 0) {
                throw new IllegalArgumentException("SMS verification code exceeds the limit");
            }
        }

        String dayLimit = redisTemplate.opsForValue().get(redisKeyDay);
        if (dayLimit == null) {
            redisTemplate.opsForValue().set(redisKeyDay, String.valueOf(smsCodeDayCount));
            redisTemplate.expire(redisKeyDay, 86400, TimeUnit.SECONDS);
        } else {
            if (Integer.parseInt(dayLimit) == 0) {
                throw new IllegalArgumentException("Sending SMS verification codes on the same day cannot exceed the limit of 15 times");
            }
        }

        String hourLimit = redisTemplate.opsForValue().get(redisKeyHour);
        if (hourLimit == null) {
            redisTemplate.opsForValue().set(redisKeyHour, String.valueOf(smsCodeHourCount));
            redisTemplate.expire(redisKeyHour, 3600, TimeUnit.SECONDS);
        } else {
            if (Integer.parseInt(hourLimit) == 0) {
                throw new IllegalArgumentException("Sending SMS verification codes in the current hour cannot exceed the limit of 10 times");
            }
        }

        if (redisTemplate.opsForValue().get(redisKeyMinute) != null) {
            throw new IllegalArgumentException("Please wait a moment while trying to send.");
        }
    }

    public List<Object> getSmsCodeRecord(String phoneNumber) {

        try {
            String redisKey = String.format(RedisKeyConstants.PHONE_SMS_CODE_RECORD, phoneNumber);
            Set<Object> fields = redisTemplate.opsForHash().keys(redisKey);
            fields.forEach(field -> {
                String strField = (String) field;
                Long expire = redisTemplate.getExpire(strField);
                if (expire != null) {
                    if (expire < 0) {
                        redisTemplate.opsForHash().delete(redisKey, strField);
                    }
                }
            });

            return redisTemplate.opsForHash().values(redisKey);
        } catch (Exception e) {
            log.error("get phone sms code validity failed. phoneNUmber : {}, Message - {}", phoneNumber, e.getMessage());
        }

        return null;
    }

    @Override public Boolean checkIfUidExists(String agoraUid) {
        String redisKey = String.format("inside:app:save:agorauid:%s", agoraUid);

        boolean result;
        try {
            String uid = redisTemplate.opsForValue().get(redisKey);
            result = uid != null;
        } catch (Exception e) {
            result = false;
            log.error("check if uid exists failed. agoraUid : {}, Message - {}", agoraUid, e.getMessage());
        }

        return result;
    }

    @Override public void saveUid(String agoraUid) {
        String redisKey = String.format("inside:app:save:agorauid:%s", agoraUid);

        try {
            redisTemplate.opsForValue().set(redisKey, agoraUid, Duration.ofSeconds(agoraTokenExpireTime));
        } catch (Exception e) {
            log.error("save uid failed. agoraUid : {}, Message - {}", agoraUid, e.getMessage());
        }
    }

    public static long getRemainSecondsOneDay() {
        Date date = new Date();
        LocalDateTime midnight = LocalDateTime.ofInstant(date.toInstant(),
                        ZoneId.systemDefault()).plusDays(1).withHour(0).withMinute(0)
                .withSecond(0).withNano(0);
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(date.toInstant(),
                ZoneId.systemDefault());
        return ChronoUnit.SECONDS.between(currentDateTime, midnight);
    }
}
