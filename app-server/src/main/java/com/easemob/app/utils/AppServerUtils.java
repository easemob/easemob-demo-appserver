package com.easemob.app.utils;

import org.apache.commons.lang.StringUtils;

public class AppServerUtils {
    private static final String REGEX_MOBILE = "^((1[1-9][0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";

    public static boolean isPhoneNumber(String phoneNumber){
        if (StringUtils.isBlank(phoneNumber)) {
            throw new IllegalArgumentException("phone number cannot be empty");
        }

        if (phoneNumber.matches(REGEX_MOBILE)){
            return true;
        } else {
            throw new IllegalArgumentException("phone number illegal");
        }
    }
}
