package com.easemob.app.service;

public interface SmsCodeService {

    /**
     * 发送短信
     *
     * @param phoneNumber phoneNumber
     * @return void
     */
    void sendSms(String phoneNumber, String resourceIp);
}
