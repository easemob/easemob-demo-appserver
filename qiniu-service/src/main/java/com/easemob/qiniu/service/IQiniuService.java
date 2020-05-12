package com.easemob.qiniu.service;

/**
 * @author shenchong@easemob.com 2020/5/11
 */
public interface IQiniuService {

    /**
     * generates RTMP publish URL
     *
     * @param domain 推流域名
     * @param hub 直播空间名
     * @param streamKey 流名
     * @param expireAfterSeconds URL will be invalid after expireAfterSeconds.
     * @return RTMP publish URL
     */
    String RTMPPublishURL(String domain, String hub, String streamKey, int expireAfterSeconds);

    /**
     * generates play URL
     *
     * @param protocol play URL 协议
     * @param domain 推流域名
     * @param hub 直播空间名
     * @param streamKey 流名
     * @return play URL
     */
    String playURL(String protocol, String domain, String hub, String streamKey);
}
