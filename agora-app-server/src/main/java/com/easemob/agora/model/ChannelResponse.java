package com.easemob.agora.model;

import lombok.Data;

@Data
public class ChannelResponse {
    private ResCode code = ResCode.RES_0K;
    private String channelName;
    private Object result;
}
