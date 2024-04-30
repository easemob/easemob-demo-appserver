package com.easemob.app.api;

import cn.hutool.extra.servlet.ServletUtil;
import com.easemob.app.model.ResCode;
import com.easemob.app.model.ResponseParam;
import com.easemob.app.service.RedisService;
import com.easemob.app.service.SmsCodeService;
import com.easemob.app.utils.AppServerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class SmsCodeController {

    @Autowired
    private SmsCodeService smsCodeService;

    @Autowired
    private RedisService redisService;

    @PostMapping("/inside/app/sms/send/{phoneNumber}")
    public ResponseEntity sendSms(@PathVariable("phoneNumber") String phoneNumber) {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String clientIP = ServletUtil.getClientIP(request);

        AppServerUtils.isPhoneNumber(phoneNumber);

//        redisService.checkSmsCodeLimit(phoneNumber, clientIP);

        ResponseParam responseParam = new ResponseParam();

        // 需要自己集成发送短信服务
//        smsCodeService.sendSms(phoneNumber, clientIP);
        responseParam.setCode(ResCode.RES_OK.getCode());
        return ResponseEntity.ok(responseParam);
    }
}
