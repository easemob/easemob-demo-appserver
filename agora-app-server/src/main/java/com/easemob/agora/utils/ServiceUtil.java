package com.easemob.agora.utils;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

/**
 * @author skyfour
 * @date 2021/2/18
 * @email skyzhang@easemob.com
 */
public class ServiceUtil {
    private ServiceUtil() {}

    private static final Pattern PATTERN_APPKEY = Pattern.compile(".+#.+");

    /**
     * get org info from appkey
     *
     * @param appkey
     * @return
     */
    public static String getOrg(String appkey) {

        //  match regex, get substring before '#'
        if (PATTERN_APPKEY.matcher(appkey).matches()) {
            return StringUtils
                    .substring(appkey, 0, appkey.indexOf("#"));
        }

        return "";
    }

    /**
     * get app info from appkey
     *
     * @param appkey
     * @return
     */
    public static String getApp(String appkey) {

        //  match regex, get substring before '#'
        if (PATTERN_APPKEY.matcher(appkey).matches()) {
            return StringUtils.substringAfterLast(appkey, "#");
        }
        return "";
    }
}
