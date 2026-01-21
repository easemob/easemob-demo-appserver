package com.easemob.app.api;
import com.easemob.app.model.enums.ResCode;
import com.easemob.app.model.response.ResponseParam;
import com.easemob.app.utils.AppServerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class SmsCodeController {


    @PostMapping("/inside/app/sms/send/{phoneNumber}")
    public ResponseEntity<ResponseParam> sendSms(@PathVariable("phoneNumber") String phoneNumber) {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes instanceof ServletRequestAttributes servletRequestAttributes
                ? servletRequestAttributes.getRequest()
                : null;
        String clientIP = resolveClientIp(request);

        log.info("Receive SMS request for phone number: {}, client IP: {}", phoneNumber, clientIP);

        AppServerUtils.isPhoneNumber(phoneNumber);

//        redisService.checkSmsCodeLimit(phoneNumber, clientIP);

        ResponseParam responseParam = new ResponseParam();

        // 需要自己集成发送短信服务
//        smsCodeService.sendSms(phoneNumber, clientIP);
        responseParam.setCode(ResCode.RES_OK.getCode());
        return ResponseEntity.ok(responseParam);
    }

    private static String resolveClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        String ip = headerIp(request, "X-Forwarded-For");
        if (!ip.isEmpty()) {
            return firstIp(ip);
        }

        ip = headerIp(request, "X-Real-IP");
        if (!ip.isEmpty()) {
            return ip;
        }

        ip = headerIp(request, "Proxy-Client-IP");
        if (!ip.isEmpty()) {
            return ip;
        }

        ip = headerIp(request, "WL-Proxy-Client-IP");
        if (!ip.isEmpty()) {
            return ip;
        }

        ip = headerIp(request, "HTTP_CLIENT_IP");
        if (!ip.isEmpty()) {
            return ip;
        }

        ip = headerIp(request, "HTTP_X_FORWARDED_FOR");
        if (!ip.isEmpty()) {
            return firstIp(ip);
        }

        String remoteAddr = request.getRemoteAddr();
        return remoteAddr == null ? "" : remoteAddr;
    }

    private static String headerIp(HttpServletRequest request, String headerName) {
        String value = request.getHeader(headerName);
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty() || "unknown".equalsIgnoreCase(trimmed)) {
            return "";
        }
        return trimmed;
    }

    private static String firstIp(String ipList) {
        int commaIndex = ipList.indexOf(',');
        String first = commaIndex >= 0 ? ipList.substring(0, commaIndex) : ipList;
        return first.trim();
    }
}
