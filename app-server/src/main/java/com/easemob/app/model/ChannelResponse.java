package com.easemob.app.model;

import lombok.Data;

@Data
public class ChannelResponse {
    private int code = ResCode.RES_OK.getCode();
    private String channelName;
    private Object result;
}
