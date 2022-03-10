package com.easemob.agora.cdn.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "easemob.agora.cdn")
public class AgoraCdnProperties {

    /**
     * 声网 appid
     */
    private String appid;

    /**
     * 声网 直播推流域名
     */
    private String pushDomain;

    /**
     * 声网 直播播放域名，rtmp协议
     */
    private String rtmpDomain;

    /**
     * 声网 直播播放域名，flv协议
     */
    private String flvDomain;

    /**
     * 声网 直播播放域名，hls协议
     */
    private String hlsDomain;

    /**
     * 声网 发布点
     */
    private String pushPoint;

    /**
     * 声网 直播推流地址过期时间，单位秒
     */
    private int expire;
}
