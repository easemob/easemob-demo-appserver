package com.easemob.app.model;

import lombok.Data;

@Data
public class ChannelResponse {
    private ResCode code = ResCode.RES_OK;
    private String channelName;
    private Object result;
}
