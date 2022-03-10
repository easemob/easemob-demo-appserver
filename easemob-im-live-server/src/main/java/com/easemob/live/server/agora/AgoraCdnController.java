package com.easemob.live.server.agora;

import com.easemob.agora.cdn.service.AgoraCdnService;
import com.easemob.live.server.utils.RequestUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * 集成声网 CDN 直播云服务，如不需要，可移除该controller
 *
 */
@RestController
public class AgoraCdnController {

    private final AgoraCdnService agoraCdnService;

    public AgoraCdnController(AgoraCdnService agoraCdnService) {
        this.agoraCdnService = agoraCdnService;
    }

    /**
     * 获取推流地址
     *
     * @param domain 推流域名
     * @param pushPoint 发布点
     * @param streamKey 流名
     * @param expire URL will be invalid after expire seconds.
     */
    @GetMapping("/appserver/agora/cdn/streams/url/push")
    public ResponseEntity getRTMPPublishURL(@RequestParam(name = "domain") String domain,
                                            @RequestParam(name = "pushPoint") String pushPoint,
                                            @RequestParam(name = "streamKey") String streamKey,
                                            @RequestParam(name = "expire", required = false) Integer expire,
                                            HttpServletRequest request) {

        RequestUtils.resolveAuthorizationToken(request.getHeader(AUTHORIZATION));

        Map<String, Object> response = new HashMap<>(1);

        String publishUrl = agoraCdnService.rtmpPushURL(domain, pushPoint, streamKey, expire);

        response.put("data", publishUrl);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取播放地址
     *
     * @param protocol play URL 协议
     * @param domain 播放域名
     * @param pushPoint 发布点
     * @param streamKey 流名
     */
    @GetMapping("/appserver/agora/cdn/streams/url/play")
    public ResponseEntity getPlayURL(@RequestParam(name = "protocol", required = false, defaultValue = "rtmp") String protocol,
                                     @RequestParam(name = "domain") String domain,
                                     @RequestParam(name = "pushPoint") String pushPoint,
                                     @RequestParam(name = "streamKey") String streamKey,
                                     HttpServletRequest request) {

        RequestUtils.resolveAuthorizationToken(request.getHeader(AUTHORIZATION));

        Map<String, Object> response = new HashMap<>(1);

        String playUrl = agoraCdnService.playURL(protocol, domain, pushPoint, streamKey);

        response.put("data", playUrl);

        return ResponseEntity.ok(response);
    }
}
