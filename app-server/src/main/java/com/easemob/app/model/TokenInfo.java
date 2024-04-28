package com.easemob.app.model;

import lombok.Data;

/**
 * @author skyfour
 * @date 2021/2/9
 * @email skyzhang@easemob.com
 */
@Data
public class TokenInfo {
    private String token;

    private Long expireTimestamp;

    private String agoraUid;
}
