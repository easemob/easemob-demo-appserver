package com.easemob.live.server.liveroom.event;

import com.easemob.live.server.liveroom.model.LiveRoomDetailsRepository;
import com.easemob.live.server.liveroom.exception.LiveRoomNotFoundException;
import com.easemob.live.server.liveroom.model.LiveRoomDetails;
import com.easemob.live.server.liveroom.model.LiveRoomStatus;
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

    private Map<String, Future> tasks = new ConcurrentHashMap<>();

    private final LiveRoomDetailsRepository liveRoomRepository;

    private final IQiniuService qiniuService;

    public LiveRoomEventHandler(LiveRoomDetailsRepository liveRoomRepository,
                                IQiniuService qiniuService) {
        this.liveRoomRepository = liveRoomRepository;
        this.qiniuService = qiniuService;
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

        FutureRunnable runnable = new FutureRunnable(liveroomId);
        ScheduledFuture future = scheduled
                .scheduleWithFixedDelay(runnable, 60000, 60000, TimeUnit.MILLISECONDS);
        runnable.setFuture(future);
        tasks.put(liveroomId, future);
    }

    private void offlineEventHandler(String liveroomId) {
        Future future = this.tasks.remove(liveroomId);
        if (future != null) {
            future.cancel(true);
        }
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
            }

            if (!qiniuService.streamOngoing(liveroomId)) {

                liveRoomDetails.setStatus(LiveRoomStatus.OFFLINE);
                liveRoomRepository.save(liveRoomDetails);

                log.info("stream is offline, so offline liveroom, too, liveroomId={}", liveroomId);

                if (future != null) {
                    future.cancel(true);
                }
            }
        }
    }
}
