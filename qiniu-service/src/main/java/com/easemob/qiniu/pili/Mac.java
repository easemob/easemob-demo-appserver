package com.easemob.qiniu.pili;

import com.easemob.qiniu.pili.utils.HMac;
import com.easemob.qiniu.pili.utils.UrlSafeBase64;

final class Mac {
    private String accessKey;
    private String secretKey;

    public Mac(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String sign(String data) throws Exception {
        byte[] sum = HMac.HmacSHA1Encrypt(data, this.secretKey);
        String sign = UrlSafeBase64.encodeToString(sum);
        return this.accessKey + ":" + sign;
    }
}
