package com.easemob.live.server.qiniu;

import com.easemob.live.server.utils.RequestUtils;
import com.easemob.qiniu.service.IQiniuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * 集成七牛直播云服务，如不需要，可移除该controller
 *
 * @author shenchong@easemob.com 2020/5/11
 */
@RestController
public class QiniuController {

    private final IQiniuService qiniuService;

    public QiniuController(IQiniuService qiniuService) {
        this.qiniuService = qiniuService;
    }

    /**
     * 获取推流地址
     *
     * @param domain 推流域名
     * @param hub 直播空间名
     * @param streamKey 流名
     * @param expire URL will be invalid after expire seconds.
     */
    @GetMapping("/appserver/streams/url/publish")
    public ResponseEntity getRTMPPublishURL(@RequestParam(name = "domain") String domain,
                                            @RequestParam(name = "hub") String hub,
                                            @RequestParam(name = "streamKey") String streamKey,
                                            @RequestParam(name = "expire", required = false) Integer expire,
                                            HttpServletRequest request) {

        RequestUtils.resolveAuthorizationToken(request.getHeader(AUTHORIZATION));

        if (expire == null) {
            expire = 600;
        }

        Map<String, Object> response = new HashMap<>(1);
        response.put("data", qiniuService.RTMPPublishURL(domain, hub, streamKey, expire));

        return ResponseEntity.ok(response);
    }

    /**
     * 获取播放地址
     *
     * @param protocol play URL 协议
     * @param domain 播放域名
     * @param hub 直播空间名
     * @param streamKey 流名
     */
    @GetMapping("/appserver/streams/url/play")
    public ResponseEntity getPlayURL(@RequestParam(name = "protocol", defaultValue = "rtmp", required = false) String protocol,
                                     @RequestParam(name = "domain") String domain,
                                     @RequestParam(name = "hub") String hub,
                                     @RequestParam(name = "streamKey") String streamKey,
                                     HttpServletRequest request) {

        RequestUtils.resolveAuthorizationToken(request.getHeader(AUTHORIZATION));

        Map<String, Object> response = new HashMap<>(1);

        String playUrl = qiniuService.playURL(protocol, domain, hub, streamKey);

        response.put("data", playUrl);

        return ResponseEntity.ok(response);
    }
}
