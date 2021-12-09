package com.easemob.agora.cdn.service;

import com.easemob.agora.cdn.config.AgoraCdnProperties;
import com.easemob.agora.cdn.enums.PlayProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

@Slf4j
@Component
public class AgoraCdnServiceImpl implements AgoraCdnService {

    private final String pushDomain;

    private final String rtmpDomain;

    private final String flvDomain;

    private final String hlsDomain;

    private final String pushPoint;

    private final int expire;

    public AgoraCdnServiceImpl(AgoraCdnProperties properties) {

        this.pushDomain = properties.getPushDomain();

        this.rtmpDomain = properties.getRtmpDomain();

        this.flvDomain = properties.getFlvDomain();

        this.hlsDomain = properties.getHlsDomain();

        this.pushPoint = properties.getPushPoint();

        this.expire = properties.getExpire();
    }

    @Override
    public String rtmpPushURL(String domain, String pushPoint, String streamKey, Integer expire) {

        domain = domain == null ? this.pushDomain : domain;

        pushPoint = pushPoint == null ? this.pushPoint : pushPoint;

        expire = expire == null ? this.expire : expire;

        return assemblePushURL(domain, pushPoint, streamKey, expire);
    }

    @Override
    public String playURL(String protocol, String domain, String pushPoint, String streamKey) {

        pushPoint = pushPoint == null ? this.pushPoint : pushPoint;

        PlayProtocol playProtocol;

        try {
            playProtocol = PlayProtocol.valueOf(protocol.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported protocol");
        }

        return assemblePlayURL(playProtocol, domain, pushPoint, streamKey, expire);
    }

    private String assemblePushURL(String domain, String pushPoint, String streamKey, int expireAfterSeconds) {

        long expire = System.currentTimeMillis() / 1000 + expireAfterSeconds;

        String path = String.format("/%s/%s", pushPoint, streamKey);

        return String.format("rtmp://%s%s?ts=%d&sign=%s", domain, path, expire, pushSign(path, expire));
    }

    private String assemblePlayURL(PlayProtocol playProtocol, String domain, String pushPoint, String streamKey, int expireAfterSeconds) {

        long expire = System.currentTimeMillis() / 1000 + expireAfterSeconds;

        String path;

        switch (playProtocol) {

            case RTMP:
                domain = domain == null ? this.rtmpDomain : domain;

                path = String.format("/%s/%s", pushPoint, streamKey);

                return String.format("rtmp://%s%s?ts=%d&sign=%s", domain, path, expire, pullSign(path, expire));
            case FLV:
                domain = domain == null ? this.flvDomain : domain;

                path = String.format("/%s/%s.flv", pushPoint, streamKey);

                return String.format("http://%s%s?ts=%d&sign=%s", domain, path, expire, pullSign(path, expire));
            case HLS:
                domain = domain == null ? this.hlsDomain : domain;

                path = String.format("/%s/%s/playlist.m3u8", pushPoint, streamKey);

                return String.format("http://%s%s?ts=%d&sign=%s", domain, path, expire, pullSign(path, expire));
            default:
                throw new IllegalArgumentException("Unsupported protocol");
        }

    }

    private String pushSign(String urlPath, long expire) {

        String sign = "agora-cdn-push-stream" + urlPath + expire;

        return DigestUtils.md5DigestAsHex(sign.getBytes()).substring(8, 24);
    }

    private String pullSign(String urlPath, long expire) {

        String sign = "agora-cdn-pull-stream" + urlPath + expire;

        return DigestUtils.md5DigestAsHex(sign.getBytes()).substring(8, 24);
    }

}
