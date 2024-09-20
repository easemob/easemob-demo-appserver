package com.easemob.app.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OneToOneVideoMatchInfo {

    private String channelName;

    private String agoraUid;

    private String rtcToken;

    private String matchedUser;

    private String matchedChatUser;
}
