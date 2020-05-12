package com.easemob.qiniu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author shenchong@easemob.com 2020/5/11
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "easemob.qiniu")
public class QiniuProperties {

    /**
     * 七牛云 Access Key
     */
    private String accessKey;

    /**
     * 七牛云 Secret Key
     */
    private String secretKey;

    /**
     * 七牛云 直播推流域名
     */
    private String publishDomain;

    /**
     * 七牛云 直播播放域名，rtmp协议
     */
    private String rtmpDomain;

    /**
     * 七牛云 直播播放域名，hls协议
     */
    private String hlsDomain;

    /**
     * 七牛云 直播播放域名，hdl协议
     */
    private String hdlDomain;

    /**
     * 七牛云 直播空间名称
     */
    private String hub;

    /**
     * 七牛云 直播推流地址过期时间，单位秒
     */
    private int expire = 600;
}
