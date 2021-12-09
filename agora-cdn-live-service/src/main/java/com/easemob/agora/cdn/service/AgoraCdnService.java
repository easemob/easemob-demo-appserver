package com.easemob.agora.cdn.service;

public interface AgoraCdnService {

    /**
     * generates RTMP push URL
     *
     * @param domain 推流域名
     * @param pushPoint 发布点
     * @param streamKey 流名
     * @param expire URL will be invalid after expire seconds.
     * @return RTMP Push URL
     */
    String rtmpPushURL(String domain, String pushPoint, String streamKey, Integer expire);

    /**
     * generates play URL
     *
     * @param protocol play URL 协议
     * @param domain 推流域名
     * @param pushPoint 发布点
     * @param streamKey 流名
     * @return play URL
     */
    String playURL(String protocol, String domain, String pushPoint, String streamKey);

}
