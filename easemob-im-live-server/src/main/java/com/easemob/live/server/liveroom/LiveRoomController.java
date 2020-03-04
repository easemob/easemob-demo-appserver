package com.easemob.live.server.liveroom;

import com.easemob.live.server.liveroom.service.LiveRoomService;
import com.easemob.live.server.utils.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * @author shenchong@easemob.com 2020/2/19
 */
@Slf4j
@RestController
public class LiveRoomController {

    private final LiveRoomService liveRoomService;
    private final Integer batchMaxSize;

    public LiveRoomController(LiveRoomService liveRoomService,
                              LiveRoomProperties properties) {
        this.liveRoomService = liveRoomService;
        this.batchMaxSize = properties.getBatchMaxSize();
    }

    /**
     * 创建直播间
     */
    @PostMapping("/appserver/liverooms")
    public ResponseEntity createLiveRoom(@RequestBody @Valid CreateLiveRoomRequest requestBody,
                                         HttpServletRequest request) {

        RequestUtils.resolveAuthorizationToken(request.getHeader(AUTHORIZATION));

        return ResponseEntity.ok(liveRoomService.createLiveRoom(requestBody));
    }

    /**
     * 获取直播间详情
     */
    @GetMapping("/appserver/liverooms/{liveroomId}")
    public ResponseEntity getLiveRoomInfo(@PathVariable("liveroomId") String liveroomId,
                                          HttpServletRequest request) {

        String token = RequestUtils
                .resolveAuthorizationToken(request.getHeader(AUTHORIZATION));

        return ResponseEntity.ok(liveRoomService.getLiveRoomInfo(liveroomId, token));
    }

    /**
     * 根据直播间创建时间逆序获取直播间列表
     * @param limit 默认为10，最大为100
     * @param cursor 默认为Long.MAX_VALUE
     */
    @GetMapping("/appserver/liverooms")
    public ResponseEntity getLiveRooms(@RequestParam(name = "limit", required = false) Integer limit,
                                       @RequestParam(name = "cursor", required = false,
                                               defaultValue = "9223372036854775807") String cursor,
                                       HttpServletRequest request) {

        limit = getLimit(limit);
        RequestUtils.resolveAuthorizationToken(request.getHeader(AUTHORIZATION));

        List<LiveRoomInfo> liveRoomInfos = liveRoomService.getLiveRooms(Long.valueOf(cursor), limit);

        LiveRoomListResponse response = new LiveRoomListResponse();
        response.setEntities(liveRoomInfos);
        response.setCount(liveRoomInfos.size());

        if (!liveRoomInfos.isEmpty() && liveRoomInfos.size() == limit) {
            response.setCursor(liveRoomInfos.get(liveRoomInfos.size() - 1).getId());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 根据直播间创建时间逆序获取正在直播的直播间列表
     * @param limit 默认为10，最大为100
     * @param cursor 默认为Long.MAX_VALUE
     */
    @GetMapping("/appserver/liverooms/ongoing")
    public ResponseEntity getOngoingLiveRooms(@RequestParam(name = "limit", required = false) Integer limit,
                                              @RequestParam(name = "cursor", required = false,
                                                      defaultValue = "9223372036854775807") String cursor,
                                              HttpServletRequest request) {

        limit = getLimit(limit);
        RequestUtils.resolveAuthorizationToken(request.getHeader(AUTHORIZATION));

        List<LiveRoomInfo> liveRoomInfos = liveRoomService.getOngoingLiveRooms(Long.valueOf(cursor), limit);

        LiveRoomListResponse response = new LiveRoomListResponse();
        response.setEntities(liveRoomInfos);
        response.setCount(liveRoomInfos.size());

        if (!liveRoomInfos.isEmpty() && liveRoomInfos.size() == limit) {
            response.setCursor(liveRoomInfos.get(liveRoomInfos.size() - 1).getId());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 开始直播
     * @param username 开始直播用户的环信ID
     */
    @PostMapping("/appserver/liverooms/{liveroomId}/users/{username}/ongoing")
    public ResponseEntity ongoingLiveRoom(@PathVariable("liveroomId") String liveroomId,
                                          @PathVariable("username") String username,
                                          HttpServletRequest request) {

        RequestUtils.resolveAuthorizationToken(request.getHeader(AUTHORIZATION));

        return ResponseEntity.ok(liveRoomService.ongoingLiveRoom(username, liveroomId));
    }

    /**
     * 结束直播
     * @param username 结束直播用户的环信ID
     */
    @PostMapping("/appserver/liverooms/{liveroomId}/users/{username}/offline")
    public ResponseEntity offlineLiveRoom(@PathVariable("liveroomId") String liveroomId,
                                          @PathVariable("username") String username,
                                          HttpServletRequest request) {

        RequestUtils.resolveAuthorizationToken(request.getHeader(AUTHORIZATION));

        return ResponseEntity.ok(liveRoomService.offlineLiveRoom(username, liveroomId));
    }

    /**
     * 修改直播间详情
     */
    @PutMapping("/appserver/liverooms/{liveroomId}")
    public ResponseEntity modifyLiveRoomInfo(@RequestBody @Valid LiveRoomRequest requestBody,
                                             @PathVariable("liveroomId") String liveroomId,
                                             HttpServletRequest request) {

        String token = RequestUtils
                .resolveAuthorizationToken(request.getHeader(AUTHORIZATION));

        return ResponseEntity.ok(liveRoomService.modifyLiveRoom(requestBody, liveroomId, token));
    }

    /**
     * 转让直播间
     * @param newOwner 直播间的新owner
     */
    @PutMapping("/appserver/liverooms/{liveroomId}/owner/{newOwner}")
    public ResponseEntity assignLiveRoomOwner(@PathVariable("liveroomId") String liveroomId,
                                              @PathVariable("newOwner") String newOwner,
                                              HttpServletRequest request) {

        String token = RequestUtils
                .resolveAuthorizationToken(request.getHeader(AUTHORIZATION));

        return ResponseEntity.ok(liveRoomService.assignLiveRoomOwner(liveroomId, newOwner, token));
    }

    /**
     * 删除直播间
     */
    @DeleteMapping("/appserver/liverooms/{liveroomId}")
    public ResponseEntity deleteLiveRoom(@PathVariable("liveroomId") String liveroomId,
                                         HttpServletRequest request) {

        String token = RequestUtils
                .resolveAuthorizationToken(request.getHeader(AUTHORIZATION));

        return ResponseEntity.ok(liveRoomService.deleteLiveRoom(liveroomId, token));
    }

    private int getLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return 10;
        }
        return limit > batchMaxSize ? batchMaxSize : limit;
    }
}
