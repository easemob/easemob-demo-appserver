package com.easemob.qiniu.pili;

public final class Client {
    private Mac mac;

    public Client(String accessKey, String secretKey) {
        this.mac = new Mac(accessKey, secretKey);
    }

    public String RTMPPublishURL(String domain, String hub, String streamKey, int expireAfterSeconds) {
        long expire = System.currentTimeMillis() / 1000 + expireAfterSeconds;
        String path = String.format("/%s/%s?e=%d", hub, streamKey, expire);
        String token;
        try {
            token = this.mac.sign(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return String.format("rtmp://%s%s&token=%s", domain, path, token);
    }

    public String RTMPPlayURL(String domain, String hub, String streamKey) {
        return String.format("rtmp://%s/%s/%s", domain, hub, streamKey);
    }

    public String HLSPlayURL(String domain, String hub, String streamKey) {
        return String.format("http://%s/%s/%s.m3u8", domain, hub, streamKey);
    }

    public String HDLPlayURL(String domain, String hub, String streamKey) {
        return String.format("http://%s/%s/%s.flv", domain, hub, streamKey);
    }
}









