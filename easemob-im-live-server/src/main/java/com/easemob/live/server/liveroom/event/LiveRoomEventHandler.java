package com.easemob.live.server.liveroom.event;

import com.easemob.live.server.liveroom.api.LiveRoomInfo;
import com.easemob.live.server.liveroom.model.LiveRoomDetailsRepository;
import com.easemob.live.server.liveroom.exception.LiveRoomNotFoundException;
import com.easemob.live.server.liveroom.model.LiveRoomDetails;
import com.easemob.live.server.liveroom.model.LiveRoomStatus;
import com.easemob.live.server.rest.RestClient;
import com.easemob.qiniu.service.IQiniuService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

/**
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

    public LiveRoomEventHandler(LiveRoomDetailsRepository liveRoomRepository,
                                IQiniuService qiniuService,
                                RestClient restClient) {
        this.liveRoomRepository = liveRoomRepository;
        this.qiniuService = qiniuService;
        this.restClient = restClient;
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

        if (this.statusTasks.containsKey(liveroomId)) {
            return;
        }

        FutureRunnable runnable = new FutureRunnable(liveroomId);
        ScheduledFuture statusFuture = scheduled
                .scheduleWithFixedDelay(runnable, 60000, 60000, TimeUnit.MILLISECONDS);
        runnable.setFuture(statusFuture);
        statusTasks.put(liveroomId, statusFuture);
    }

    private void offlineEventHandler(String liveroomId) {
        Future statusFuture = this.statusTasks.remove(liveroomId);
        if (statusFuture != null) {
            statusFuture.cancel(true);
        }

        cleanupLiveRoom(liveroomId);
    }

    private void cleanupLiveRoom(String liveroomId) {

        Future cleanupFuture = this.cleanupTasks.remove(liveroomId);
        if (cleanupFuture != null) {
            cleanupFuture.cancel(true);
        }

        cleanupFuture = scheduled.schedule(() -> {

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
        }, 3600000, TimeUnit.MILLISECONDS);

        cleanupTasks.put(liveroomId, cleanupFuture);
    }

    public class FutureRunnable implements Runnable {

        @Setter
        private Future<?> future;

        private final String liveroomId;

        public FutureRunnable(String liveroomId) {
            this.liveroomId = liveroomId;
        }

        @Override
        public void run() {

            LiveRoomDetails liveRoomDetails =
                    liveRoomRepository.findById(Long.valueOf(liveroomId))
                            .orElseThrow(() -> new LiveRoomNotFoundException(
                                    "liveroom " + liveroomId + " is not found"));

            if (liveRoomDetails.getStatus() == LiveRoomStatus.OFFLINE && future != null) {
                future.cancel(true);
                return;
            }

            if (!qiniuService.streamOngoing(liveroomId)) {

                liveRoomDetails.setStatus(LiveRoomStatus.OFFLINE);
                refreshLiveRoomInfo(liveRoomDetails);

                cleanupLiveRoom(liveroomId);

                log.info("stream is offline, so offline liveroom, too, liveroomId={}", liveroomId);

                if (future != null) {
                    future.cancel(true);
                }
                return;
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
