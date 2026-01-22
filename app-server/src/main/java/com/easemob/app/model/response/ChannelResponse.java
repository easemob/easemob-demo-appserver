package com.easemob.app.model.response;

import com.easemob.app.model.enums.ResCode;
import lombok.Data;

@Data
public class ChannelResponse {
    /**
     * 响应码
     */
    private int code = ResCode.RES_OK.getCode();
    /**
     * 频道名称
     */
    private String channelName;
    /**
     * 结果数据
     */
    private Object result;
}
