package com.easemob.live.server.liveroom.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author shenchong@easemob.com 2020/2/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "LIVE_ROOM_DETAILS")
public class LiveRoomDetails {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created")
    private Long created;

    @Column(name = "owner")
    private String owner;

    @Column(name = "cover")
    private String cover;

    @Column(name = "persistent")
    private Boolean persistent;

    @Enumerated
    @Column(name = "status", nullable = false)
    private LiveRoomStatus status = LiveRoomStatus.OFFLINE;

    @Column(name = "showid")
    private Long showid = 0L;

    @Column(name = "affiliations_count")
    private Integer affiliationsCount;

    @Column(name = "ext")
    private String ext;
}
