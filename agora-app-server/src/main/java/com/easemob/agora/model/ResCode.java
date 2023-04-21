package com.easemob.agora.model;

/**
 * description:
 * author: lijian
 * date: 2021-01-20
 **/
public enum ResCode {
    RES_0K(200),//可以成功返回
    RES_REQUEST_PARAM_ERROR(400),//请求参数错误
    RES_METHOND_ERROR(411), //请求方法错误
    RES_PARME_ERROR(412),//请求参数错误
    RES_CONTENT_TYPE_ERROR(413), //传参类型错误
    RES_APPKEY_ERROR(414),  //appkey错误
    RES_UNAUTHORIDZE_ERROR(401), //此用户不存在
    RES_USER_NOT_FOUND(404), //表示未授权[无token、token错误、token过期]
    RES_OTHER(416);  //其他未知错误

    public  int code;

    ResCode(int code) {
        this.code = code;
    }

    public static ResCode getFrom(int code) {
        switch (code) {
            case 200:
                return RES_0K;
            case 411:
                return RES_METHOND_ERROR;
            case 412:
                return RES_PARME_ERROR;
            case 413:
                return RES_CONTENT_TYPE_ERROR;
            case 414:
                return RES_APPKEY_ERROR;
            case 401:
                return RES_UNAUTHORIDZE_ERROR;
            case 404:
                return RES_USER_NOT_FOUND;
            default:
                return RES_OTHER;
        }
    }
}
