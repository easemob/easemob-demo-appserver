package com.easemob.app.service.impl;

import com.easemob.app.feign.SmsCodeFeign;
import com.easemob.app.model.*;
import com.easemob.app.service.RedisService;
import com.easemob.app.service.SmsCodeService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsCodeServiceImpl implements SmsCodeService {

    @Autowired
    private SmsCodeFeign smsCodeFeign;

    @Autowired
    private RedisService redisService;

    @Override public void sendSms(String phoneNumber, String resourceIp) {

        SmsCodeRequest request  = SmsCodeRequest.builder()
                .checkHost(Boolean.FALSE)
                .imgVerifyResult(Boolean.FALSE)
                .host(resourceIp)
                .telephone(phoneNumber)
                .build();

        GetSmsCodeResponse response;
        try {
            response = smsCodeFeign.sendSms(request);
        } catch (Exception e) {
            log.error("send sms error : {}", e.getMessage());
            throw new IllegalArgumentException("Please wait a moment while trying to send.");
        }

        try {
            redisService.saveSmsCode(phoneNumber, response.getData(), resourceIp);
        } catch (FeignException e) {
            log.error("save sms code error : {}", e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }
}
