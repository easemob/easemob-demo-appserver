package com.easemob.live.server.liveroom.event;

import com.easemob.live.server.liveroom.api.LiveRoomInfo;
import com.easemob.live.server.liveroom.api.LiveRoomProperties;
import com.easemob.live.server.liveroom.model.LiveRoomDetailsRepository;
import com.easemob.live.server.liveroom.exception.LiveRoomNotFoundException;
import com.easemob.live.server.liveroom.model.LiveRoomDetails;
import com.easemob.live.server.liveroom.model.LiveRoomStatus;
import com.easemob.live.server.liveroom.model.VideoType;
import com.easemob.live.server.rest.RestClient;
import com.easemob.qiniu.service.IQiniuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 1、直播间开播后，每60s更新直播间详情，如直播间流已断开，则关闭直播间；
 *
 * 2、直播间关闭后，1小时内状态未改变，则清除直播间（此项取决于直播间persistent为false）；
 *
 * 3、对于videoType为VOD或者agora_vod的点播直播间，不自动关闭直播间；
 *
 * @author shenchong@easemob.com 2020/10/9
 */
@Slf4j
@Service
public class LiveRoomEventHandler implements ApplicationListener<LiveRoomEvent> {

    private static final int DEFAULT_THREAD_COUNT = Math.max(Runtime.getRuntime().availableProcessors(), 4);

    private ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(DEFAULT_THREAD_COUNT);

    private Map<String, Future> statusTasks = new ConcurrentHashMap<>();

    private Map<String, Future> cleanupTasks = new ConcurrentHashMap<>();

    private final LiveRoomDetailsRepository liveRoomRepository;

    private final IQiniuService qiniuService;

    private final RestClient restClient;

    private final LiveRoomProperties properties;

    public LiveRoomEventHandler(LiveRoomDetailsRepository liveRoomRepository,
                                IQiniuService qiniuService,
                                RestClient restClient,
                                LiveRoomProperties properties) {
        this.liveRoomRepository = liveRoomRepository;
        this.qiniuService = qiniuService;
        this.restClient = restClient;
        this.properties = properties;
    }

    @Override
    public void onApplicationEvent(LiveRoomEvent event) {

        log.info("receive liveroom event, event={}", event);

        switch (event.getType()) {
            case ONGOING:

                refreshLiveRoomStatus(event.getLiveroomId());
                break;
            case OFFLINE:

                offlineEventHandler(event.getLiveroomId());
                break;
            default:
                break;
        }
    }

    private void refreshLiveRoomStatus(String liveroomId) {
        cancelCleanupTask(liveroomId);
        cancelStatusTask(liveroomId);

        ScheduledFuture statusFuture = scheduled
                .scheduleWithFixedDelay(new FutureRunnable(liveroomId),
                        properties.getStreamPingDelay(), properties.getStreamPingDelay(), TimeUnit.MILLISECONDS);
        statusTasks.put(liveroomId, statusFuture);
    }

    private void offlineEventHandler(String liveroomId) {
        cancelStatusTask(liveroomId);
        cleanupLiveRoom(liveroomId);
    }

    private void cleanupLiveRoom(String liveroomId) {

        cancelCleanupTask(liveroomId);

        Future cleanupFuture = scheduled.schedule(() -> {

            cleanupTasks.remove(liveroomId);

            LiveRoomDetails liveRoomDetails =
                    liveRoomRepository.findById(Long.valueOf(liveroomId))
                            .orElseThrow(() -> new LiveRoomNotFoundException(
                                    "liveroom " + liveroomId + " is not found"));

            if (liveRoomDetails.getStatus() == LiveRoomStatus.ONGOING) {
                return;
            }

            Boolean persistence = liveRoomDetails.getPersistent();
            if (persistence != null && !persistence) {
                Boolean success = restClient.deleteChatroom(liveroomId, restClient.retrieveAppToken());
                if (!success) {
                    log.warn("delete chatroom failed, liveroomId : {}", liveroomId);
                }
                liveRoomRepository.deleteById(Long.valueOf(liveroomId));
                log.info("cleanup liveroom, liveroomId={}", liveroomId);
            }
        }, properties.getLiveroomCleanDelay(), TimeUnit.MILLISECONDS);

        cleanupTasks.put(liveroomId, cleanupFuture);
    }

    private void cancelCleanupTask(String liveroomId) {
        Future cleanupFuture = this.cleanupTasks.remove(liveroomId);
        if (cleanupFuture != null) {
            cleanupFuture.cancel(true);
        }
    }

    private void cancelStatusTask(String liveroomId) {
        Future statusFuture = this.statusTasks.remove(liveroomId);
        if (statusFuture != null) {
            statusFuture.cancel(true);
        }
    }

    public class FutureRunnable implements Runnable {

        private final String liveroomId;

        private final AtomicInteger times = new AtomicInteger(0);

        public FutureRunnable(String liveroomId) {
            this.liveroomId = liveroomId;
        }

        @Override
        public void run() {

            LiveRoomDetails liveRoomDetails =
                    liveRoomRepository.findById(Long.valueOf(liveroomId))
                            .orElseThrow(() -> new LiveRoomNotFoundException(
                                    "liveroom " + liveroomId + " is not found"));

            if (liveRoomDetails.getStatus() == LiveRoomStatus.OFFLINE) {
                cancelStatusTask(liveroomId);
                return;
            }

            boolean streamOngoing = qiniuService.streamOngoing(liveroomId);
<<<<<<< HEAD
            if (!streamOngoing && (liveRoomDetails.getVideoType() != VideoType.vod && liveRoomDetails.getVideoType() != VideoType.agora_vod)
=======
            if (!streamOngoing && (liveRoomDetails.getVideoType() != VideoType.vod) && (liveRoomDetails.getVideoType() != VideoType.agora_vod)
>>>>>>> fix agora_vod  bug
                    && times.incrementAndGet() >= 2) {

                liveRoomDetails.setStatus(LiveRoomStatus.OFFLINE);
                refreshLiveRoomInfo(liveRoomDetails);

                cleanupLiveRoom(liveroomId);

                log.info("stream is offline, so offline liveroom, too, liveroomId={}", liveroomId);

                cancelStatusTask(liveroomId);
                return;
            }

            if (streamOngoing) {
                log.info("stream is ongoing, liveroomId={}", liveroomId);
                times.set(0);
            }
            refreshLiveRoomInfo(liveRoomDetails);
        }

        private void refreshLiveRoomInfo(LiveRoomDetails oldDetails) {
            try {
                LiveRoomInfo liveRoomInfo =
                        restClient.retrieveChatroomInfo(liveroomId, restClient.retrieveAppToken());
                // 更新直播间信息
                oldDetails.setName(liveRoomInfo.getName());
                oldDetails.setDescription(liveRoomInfo.getDescription());
                oldDetails.setOwner(liveRoomInfo.getOwner());
                oldDetails.setAffiliationsCount(liveRoomInfo.getAffiliationsCount());
                LiveRoomDetails details = liveRoomRepository.save(oldDetails);
                log.info("refresh liveroom info, details={}", details);
            } catch (Exception e) {
                log.warn("update liveroom info failed, e={}", e.getMessage());
            }
        }
    }
}
