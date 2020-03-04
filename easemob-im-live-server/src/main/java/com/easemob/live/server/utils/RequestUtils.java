package com.easemob.live.server.utils;

import com.easemob.live.server.liveroom.exception.LiveRoomSecurityException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.easemob.live.server.liveroom.model.AuthErrorInfo.BAD_ACCESS_TOKEN_ERROR;

/**
 * @author shenchong@easemob.com 2020/2/20
 */
public class RequestUtils {

    private static final String BEARER_HEADER_NAME = "Bearer";
    private static final Pattern AUTH_SCHEMA_PATTERN = Pattern.compile("\\s*(\\w*)\\s+(.*)");

    /**
     * 从header中取出的auth参数截取出token字符串返回
     */
    public static String resolveAuthorizationToken(String authzHeader) {
        if (authzHeader == null) {
            throw new LiveRoomSecurityException(BAD_ACCESS_TOKEN_ERROR);
        }
        Matcher m = AUTH_SCHEMA_PATTERN.matcher(authzHeader);
        if (!m.matches()) {
            throw new LiveRoomSecurityException(BAD_ACCESS_TOKEN_ERROR);
        }

        String authSchema = m.group(1);
        if (BEARER_HEADER_NAME.equalsIgnoreCase(authSchema)) {
            return m.group(2);
        } else {
            throw new LiveRoomSecurityException(BAD_ACCESS_TOKEN_ERROR);
        }
    }
}
