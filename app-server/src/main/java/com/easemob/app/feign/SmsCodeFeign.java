package com.easemob.app.feign;

import com.easemob.app.model.GetSmsCodeResponse;
import com.easemob.app.model.SmsCodeRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${easemob.notice.application.name}", url = "${easemob.notice.url}")
public interface SmsCodeFeign {

    @PostMapping("/internal/sms/send/verify")
    GetSmsCodeResponse sendSms(@RequestBody SmsCodeRequest request);

}
