package com.easemob.live.server.liveroom.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author shenchong@easemob.com 2020/2/20
 */
@Repository
public interface LiveRoomDetailsRepository extends JpaRepository<LiveRoomDetails, Long> {

    @Query(value = "select * from live_room_details where id<?1 order by id desc limit ?2", nativeQuery = true)
    List<LiveRoomDetails> findBeforeId(Long id, int limit);

    @Query(value = "select * from live_room_details where status=1 and id<?1 order by id desc limit ?2", nativeQuery = true)
    List<LiveRoomDetails> findOngoingRoomsBeforeId(Long id, int limit);

    @Query(value = "select * from live_room_details where status=1 and video_type=0 and id<?1 order by id desc limit ?2", nativeQuery = true)
    List<LiveRoomDetails> findOngoingLiveRoomsBeforeId(Long id, int limit);

    @Query(value = "select * from live_room_details where status=1 and video_type=1 and id<?1 order by id desc limit ?2", nativeQuery = true)
    List<LiveRoomDetails> findOngoingVodRoomsBeforeId(Long id, int limit);
}
