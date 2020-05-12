package com.easemob.qiniu.service;

import com.easemob.qiniu.config.QiniuProperties;
import com.easemob.qiniu.enums.PlayProtocol;
import com.easemob.qiniu.pili.Client;
import org.springframework.stereotype.Component;

/**
 * @author shenchong@easemob.com 2020/5/11
 */
@Component
public class QiniuServiceImpl implements IQiniuService {

    private final Client client;

    public QiniuServiceImpl(QiniuProperties properties) {

        this.client = new Client(properties.getAccessKey(), properties.getSecretKey());
    }

    @Override
    public String RTMPPublishURL(String domain, String hub, String streamKey, int expireAfterSeconds) {

        return client.RTMPPublishURL(domain, hub, streamKey, expireAfterSeconds);
    }

    @Override
    public String playURL(String protocol, String domain, String hub, String streamKey) {

        PlayProtocol playProtocol;

        try {
            playProtocol = PlayProtocol.valueOf(protocol.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported protocol");
        }

        switch (playProtocol) {

            case RTMP:
                return client.RTMPPlayURL(domain, hub, streamKey);

            case HLS:
                return client.HLSPlayURL(domain, hub, streamKey);

            case HDL:
                return client.HDLPlayURL(domain, hub, streamKey);

            default:
                throw new IllegalArgumentException("Unsupported protocol");
        }
    }
}
