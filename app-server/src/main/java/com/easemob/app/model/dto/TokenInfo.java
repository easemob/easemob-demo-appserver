package com.easemob.app.model.dto;

import lombok.Data;

/**
 * @author skyfour
 * @date 2021/2/9
 * @email skyzhang@easemob.com
 */
@Data
public class TokenInfo {
    /**
     * 访问令牌
     */
    private String token;

    /**
     * 过期时间戳
     */
    private Long expireTimestamp;

    /**
     * 声网 UID
     */
    private String agoraUid;
}
