package com.easemob.agora.AgoraIO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RtcTokenGenerate {

    public static String generateToken(String appId, String appCertificate, String channelName,
            String userAccount, int expirationTimeInSeconds) {
        RtcTokenBuilder token = new RtcTokenBuilder();
        int timestamp = (int) (System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
        String result = token.buildTokenWithUserAccount(appId, appCertificate,
                channelName, userAccount, RtcTokenBuilder.Role.Role_Publisher, timestamp);
        return result;
    }
}
