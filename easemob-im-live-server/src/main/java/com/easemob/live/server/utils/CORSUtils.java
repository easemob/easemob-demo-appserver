package com.easemob.live.server.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * @author shenchong@easemob.com 2020/3/25
 */
public class CORSUtils {

    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String ACCESS_CONTROL_REQUEST_METHOD = "access-control-request-method";
    private static final String ACCESS_CONTROL_REQUEST_HEADERS = "access-control-request-headers";
    private static final String ORIGIN_HEADER = "origin";
    private static final String REFERER_HEADER = "referer";


    public static void allowAllOrigins(HttpServletRequest request, HttpServletResponse response) {

        Enumeration<String> requestMethod = request.getHeaders(ACCESS_CONTROL_REQUEST_METHOD);
        while (requestMethod != null && requestMethod.hasMoreElements()) {

            response.addHeader(ACCESS_CONTROL_ALLOW_METHODS, requestMethod.nextElement());
        }

        Enumeration<String> requestHeaders = request.getHeaders(ACCESS_CONTROL_REQUEST_HEADERS);
        while (requestHeaders != null && requestHeaders.hasMoreElements()) {
            response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, requestHeaders.nextElement());
        }

        allowOrigins(request, response);
    }

    public static void allowOrigins(HttpServletRequest request, HttpServletResponse response) {

        boolean originSent = false;
        Enumeration<String> originHeader = request.getHeaders(ORIGIN_HEADER);
        while (originHeader != null && originHeader.hasMoreElements()) {
            originSent = true;
            response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, originHeader.nextElement());
        }

        if (!originSent) {
            String origin = getOrigin(request);
            if (origin != null) {
                response.addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            } else {
                response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            }
        } else {
            response.addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }
    }

    private static String getOrigin(String origin, String referer) {
        if ((origin != null) && (!"null".equalsIgnoreCase(origin))) {
            return origin;
        }
        if ((referer != null) && (referer.startsWith("http"))) {
            int i = referer.indexOf("//");
            if (i != -1) {
                i = referer.indexOf('/', i + 2);
                if (i != -1) {
                    return referer.substring(0, i);
                } else {
                    return referer;
                }
            }
        }
        return null;
    }

    private static String getOrigin(HttpServletRequest request) {
        String origin = request.getHeader(ORIGIN_HEADER);
        String referer = request.getHeader(REFERER_HEADER);
        return getOrigin(origin, referer);
    }
}
