package com.easemob.live.server.liveroom.service;

import com.easemob.live.server.liveroom.api.CreateLiveRoomRequest;
import com.easemob.live.server.liveroom.api.LiveRoomProperties;
import com.easemob.live.server.liveroom.model.LiveRoomDetailsRepository;
import com.easemob.live.server.liveroom.api.LiveRoomInfo;
import com.easemob.live.server.liveroom.api.LiveRoomRequest;
import com.easemob.live.server.liveroom.event.LiveRoomEvent;
import com.easemob.live.server.liveroom.event.LiveRoomOfflineEvent;
import com.easemob.live.server.liveroom.event.LiveRoomOngoingEvent;
import com.easemob.live.server.liveroom.exception.ForbiddenOpException;
import com.easemob.live.server.liveroom.exception.LiveRoomNotFoundException;
import com.easemob.live.server.liveroom.model.LiveRoomDetails;
import com.easemob.live.server.liveroom.model.LiveRoomStatus;
import com.easemob.live.server.liveroom.model.VideoType;
import com.easemob.live.server.rest.RestClient;
import com.easemob.live.server.rest.chatroom.CreateChatroomRequest;
import com.easemob.live.server.rest.chatroom.ModifyChatroomRequest;
import com.easemob.live.server.utils.JsonUtils;
import com.easemob.live.server.utils.LiveRoomSchema;
import com.easemob.live.server.utils.ModelConverter;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author shenchong@easemob.com 2020/2/19
 */
@Slf4j
@Service
public class LiveRoomService {

    private RestClient restClient;

    private LiveRoomDetailsRepository liveRoomDetailsRepository;

    private ApplicationEventPublisher eventPublisher;

    private int maxAffiliationsSize;

    private Cache<String, LiveRoomInfo> infoCache = Caffeine.newBuilder()
            .initialCapacity(100)
            .maximumSize(1000)
            .expireAfterWrite(10000, TimeUnit.MILLISECONDS)
            .build();

    public LiveRoomService(RestClient restClient,
            LiveRoomDetailsRepository liveRoomDetailsRepository,
            ApplicationEventPublisher eventPublisher,
            LiveRoomProperties properties) {
        this.restClient = restClient;
        this.liveRoomDetailsRepository = liveRoomDetailsRepository;
        this.eventPublisher = eventPublisher;
        this.maxAffiliationsSize = properties.getMaxAffiliationsSize();
    }


    public LiveRoomInfo createLiveRoom(CreateLiveRoomRequest liveRoomRequest) {

        log.info("create liveroom, request : {}", liveRoomRequest);

        String token = restClient.retrieveAppToken();

        // 创建聊天室
        CreateChatroomRequest chatroomRequest = CreateChatroomRequest.builder()
                .name(liveRoomRequest.getName())
                .description(liveRoomRequest.getDescription())
                .owner(liveRoomRequest.getOwner())
                .maxUsers(liveRoomRequest.getMaxUsers())
                .members(liveRoomRequest.getMembers())
                .mute(liveRoomRequest.getMute())
                .scale(CreateChatroomRequest.Scale.LARGE)
                .build();

        String chatroomId = restClient.createChatroom(chatroomRequest, token);

        log.info("create chatroom success, chatroomId : {}", chatroomId);

        // 获取聊天室详情
        LiveRoomInfo liveRoomInfo = restClient.retrieveChatroomInfo(chatroomId, token)
                .filterLiveRoomInfo(maxAffiliationsSize);
        liveRoomInfo.setPersistent(liveRoomRequest.getPersistent());
        liveRoomInfo.setVideoType(liveRoomRequest.getVideoType());
        liveRoomInfo.setCover(liveRoomRequest.getCover());
        liveRoomInfo.setExt(liveRoomRequest.getExt());

        // 创建直播间
        LiveRoomDetails liveRoomDetails = LiveRoomDetails.builder()
                .id(Long.valueOf(liveRoomInfo.getId()))
                .name(liveRoomInfo.getName())
                .description(liveRoomInfo.getDescription())
                .created(liveRoomInfo.getCreated())
                .owner(liveRoomInfo.getOwner())
                .cover(liveRoomInfo.getCover())
                .persistent(liveRoomInfo.getPersistent())
                .videoType(liveRoomInfo.getVideoType())
                .status(liveRoomInfo.getStatus())
                .showid(liveRoomInfo.getShowid())
                .affiliationsCount(liveRoomInfo.getAffiliationsCount())
                .ext(JsonUtils.mapToJsonString(liveRoomInfo.getExt()))
                .build();

        liveRoomDetailsRepository.save(liveRoomDetails);

        log.info("create liveroom success, liveRoomInfo : {}", liveRoomInfo);

        return liveRoomInfo;
    }

    public LiveRoomInfo getLiveRoomInfo(String liveroomId, String token) {
        return infoCache.get(liveroomId, key -> getLiveRoomInfoFromDb(key, token));
    }

    public LiveRoomInfo getLiveRoomInfoFromDb(String liveroomId, String token) {

        log.info("get liveroom info, liveroomId : {}, token : {}", liveroomId, token);

        LiveRoomInfo liveRoomInfo = restClient.retrieveChatroomInfo(liveroomId, token)
                .filterLiveRoomInfo(maxAffiliationsSize);

        LiveRoomDetails oldDetails =
                liveRoomDetailsRepository.findById(Long.valueOf(liveroomId))
                        .orElseThrow(
                                () -> new LiveRoomNotFoundException(liveroomId + " is not found"));

        // 更新直播间信息
        oldDetails.setName(liveRoomInfo.getName());
        oldDetails.setDescription(liveRoomInfo.getDescription());
        oldDetails.setOwner(liveRoomInfo.getOwner());
        oldDetails.setAffiliationsCount(liveRoomInfo.getAffiliationsCount());
        LiveRoomDetails liveRoomDetails = liveRoomDetailsRepository.save(oldDetails);

        liveRoomInfo.setPersistent(liveRoomDetails.getPersistent());
        liveRoomInfo.setVideoType(liveRoomDetails.getVideoType());
        liveRoomInfo.setCover(liveRoomDetails.getCover());
        liveRoomInfo.setStatus(liveRoomDetails.getStatus());
        liveRoomInfo.setShowid(liveRoomDetails.getShowid());
        liveRoomInfo.setExt(JsonUtils.parse(liveRoomDetails.getExt(), Map.class));

        return liveRoomInfo;
    }

    public List<LiveRoomInfo> getLiveRooms(VideoType videoType, Long cursor, int limit) {

        List<LiveRoomDetails> liveRoomDetailsList = new ArrayList<>();

        if (videoType == VideoType.live) {
            liveRoomDetailsList = liveRoomDetailsRepository.findLiveRoomsBeforeId(cursor, limit);
        }

        if (videoType == VideoType.vod) {
            liveRoomDetailsList = liveRoomDetailsRepository.findVodRoomsBeforeId(cursor, limit);
        }

        if (videoType == null) {
            liveRoomDetailsList = liveRoomDetailsRepository.findBeforeId(cursor, limit);
        }

        return liveRoomDetailsList.stream()
                .map(ModelConverter::detailsConverterLiveRoomInfo)
                .collect(Collectors.toList());
    }

    public List<LiveRoomInfo> getOngoingLiveRooms(VideoType videoType, Long cursor, int limit) {

        List<LiveRoomDetails> liveRoomDetailsList = new ArrayList<>();

        if (videoType == VideoType.live) {
            liveRoomDetailsList = liveRoomDetailsRepository.findOngoingLiveRoomsBeforeId(cursor, limit);
        }

        if (videoType == VideoType.vod) {
            liveRoomDetailsList = liveRoomDetailsRepository.findOngoingVodRoomsBeforeId(cursor, limit);
        }

        if (videoType == null) {
            liveRoomDetailsList = liveRoomDetailsRepository.findOngoingRoomsBeforeId(cursor, limit);
        }

        return liveRoomDetailsList.stream()
                .map(ModelConverter::detailsConverterLiveRoomInfo)
                .collect(Collectors.toList());
    }

    public LiveRoomInfo ongoingLiveRoom(String username, String liveroomId) {

        log.info("ongoing liveroom, liveroomId : {}, username : {}", liveroomId, username);

        infoCache.invalidate(liveroomId);

        LiveRoomDetails liveRoomDetails =
                liveRoomDetailsRepository.findById(Long.valueOf(liveroomId))
                        .orElseThrow(() -> new LiveRoomNotFoundException(
                                "liveroom " + liveroomId + " is not found"));

        String token = restClient.retrieveAppToken();

        if ((liveRoomDetails.getStatus() == LiveRoomStatus.ONGOING)
                && !liveRoomDetails.getOwner().equalsIgnoreCase(username)) {
            throw new ForbiddenOpException("you are not owner to this liveroom");
        }

        if (!liveRoomDetails.getOwner().equalsIgnoreCase(username)) {

            if (liveRoomDetails.getVideoType() == VideoType.vod) {
                throw new ForbiddenOpException("you are not owner to this vod liveroom");
            }

            log.info("assign liveroom, new owner : {}, old owner : {}",
                    username, liveRoomDetails.getOwner());

            // 转让聊天室
            Boolean success = restClient.assignChatroomOwner(liveroomId, username, token);

            if (!success) {
                throw new ForbiddenOpException(
                        "liveroom " + liveroomId + " assign owner failed, please try again later");
            }
        }

        LiveRoomInfo liveRoomInfo = restClient.retrieveChatroomInfo(liveroomId, token)
                .filterLiveRoomInfo(maxAffiliationsSize);

        // 更新直播间信息
        liveRoomDetails.setStatus(LiveRoomStatus.ONGOING);
        liveRoomDetails.setShowid(liveRoomDetails.getShowid() + 1);
        liveRoomDetails.setOwner(liveRoomInfo.getOwner());
        liveRoomDetails.setName(liveRoomInfo.getName());
        liveRoomDetails.setAffiliationsCount(liveRoomInfo.getAffiliationsCount());
        liveRoomDetailsRepository.save(liveRoomDetails);

        LiveRoomEvent event = new LiveRoomOngoingEvent(liveroomId);
        eventPublisher.publishEvent(event);

        liveRoomInfo.setPersistent(liveRoomDetails.getPersistent());
        liveRoomInfo.setVideoType(liveRoomDetails.getVideoType());
        liveRoomInfo.setCover(liveRoomDetails.getCover());
        liveRoomInfo.setStatus(liveRoomDetails.getStatus());
        liveRoomInfo.setShowid(liveRoomDetails.getShowid());
        liveRoomInfo.setExt(JsonUtils.parse(liveRoomDetails.getExt(), Map.class));

        return liveRoomInfo;
    }

    public LiveRoomInfo offlineLiveRoom(String username, String liveroomId) {

        log.info("offline liveroom, liveroomId : {}, username : {}", liveroomId, username);

        infoCache.invalidate(liveroomId);

        LiveRoomDetails liveRoomDetails =
                liveRoomDetailsRepository.findById(Long.valueOf(liveroomId))
                        .orElseThrow(() -> new LiveRoomNotFoundException(
                                "liveroom " + liveroomId + " is not found"));

        if (!liveRoomDetails.getOwner().equalsIgnoreCase(username)) {
            throw new ForbiddenOpException(
                    "user " + username + " is not owner of the liveroom " + liveroomId);
        }

        liveRoomDetails.setStatus(LiveRoomStatus.OFFLINE);
        LiveRoomDetails details = liveRoomDetailsRepository.save(liveRoomDetails);

        LiveRoomEvent event = new LiveRoomOfflineEvent(liveroomId);
        eventPublisher.publishEvent(event);

        return ModelConverter.detailsConverterLiveRoomInfo(details);
    }

    public LiveRoomInfo modifyLiveRoom(LiveRoomRequest request, String liveroomId, String token) {

        log.info("modify liveroom, liveroomId : {}, request : {}", liveroomId, request);

        infoCache.invalidate(liveroomId);

        LiveRoomDetails liveRoomDetails =
                liveRoomDetailsRepository.findById(Long.valueOf(liveroomId))
                        .orElseThrow(() -> new LiveRoomNotFoundException(
                                "liveroom " + liveroomId + " is not found"));

        // 检查是否需要修改 chatroom info
        if (LiveRoomSchema.checkModifiedFieldsForChatRoom(request)) {
            ModifyChatroomRequest chatRoomRequest = ModifyChatroomRequest.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .maxUsers(request.getMaxUsers())
                    .build();
            restClient.modifyChatroom(liveroomId, chatRoomRequest, token);
        }

        liveRoomDetails = LiveRoomSchema.normalizeLiveRoomDetails(liveRoomDetails, request);
        LiveRoomDetails details = liveRoomDetailsRepository.save(liveRoomDetails);

        return ModelConverter.detailsConverterLiveRoomInfo(details);
    }

    public LiveRoomInfo assignLiveRoomOwner(String liveroomId, String newOwner, String token) {

        log.info("assign liveroom owner, liveroomId : {}, new owner : {}", liveroomId, newOwner);

        infoCache.invalidate(liveroomId);

        LiveRoomDetails liveRoomDetails =
                liveRoomDetailsRepository.findById(Long.valueOf(liveroomId))
                        .orElseThrow(() -> new LiveRoomNotFoundException(
                                "liveroom " + liveroomId + " is not found"));

        Boolean success = restClient.assignChatroomOwner(liveroomId, newOwner, token);

        if (!success) {
            log.error("assign liveroom owner failed, liveroomId : {}, new owner : {}",
                    liveroomId, newOwner);
            throw new ForbiddenOpException("assign liveroom owner failed");
        }

        liveRoomDetails.setOwner(newOwner);
        liveRoomDetails = liveRoomDetailsRepository.save(liveRoomDetails);

        return ModelConverter.detailsConverterLiveRoomInfo(liveRoomDetails);
    }

    public LiveRoomInfo deleteLiveRoom(String liveroomId, String token) {

        log.info("delete liveroom, liveroomId : {}", liveroomId);

        infoCache.invalidate(liveroomId);

        LiveRoomDetails liveRoomDetails =
                liveRoomDetailsRepository.findById(Long.valueOf(liveroomId))
                        .orElseThrow(() -> new LiveRoomNotFoundException(
                                "liveroom " + liveroomId + " is not found"));

        Boolean success = restClient.deleteChatroom(liveroomId, token);

        if (!success) {
            log.error("delete liveroom failed, liveroomId : {}, token : {}",
                    liveroomId, token);
            throw new ForbiddenOpException("delete liveroom failed");
        }

        liveRoomDetailsRepository.deleteById(liveRoomDetails.getId());

        return ModelConverter.detailsConverterLiveRoomInfo(liveRoomDetails);
    }
}
