package com.easemob.agora.model;

import lombok.Data;

/**
 * description:
 * author: lijian
 * date: 2021-01-19
 **/
@Data
public class Response {
    private  Integer  resCode;
    private  String rtcToken;
    private  String errorInfo;
}
