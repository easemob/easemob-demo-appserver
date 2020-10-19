package com.easemob.live.server.liveroom.api;

import com.easemob.live.server.liveroom.model.LiveRoomStatus;
import com.easemob.live.server.liveroom.model.VideoType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.easemob.live.server.liveroom.model.LiveRoomConstant.OWNER;

/**
 * @author shenchong@easemob.com 2020/2/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize()
public class LiveRoomInfo {

    private String id;

    private String name;

    private String description;

    private String owner;

    private Long created;

    private Boolean mute;

    private String cover;

    private Boolean persistent;

    @JsonProperty("video_type")
    private VideoType videoType;

    private LiveRoomStatus status = LiveRoomStatus.OFFLINE;

    private long showid = 0;

    @JsonProperty("maxusers")
    private Integer maxUsers;

    @JsonProperty("affiliations_count")
    private Integer affiliationsCount;

    private Map<String, Object> ext;

    @JsonProperty("affiliations")
    private List<Map<String, Object>> affiliations;

    /**
     * 因为affiliations列表中可能包含owner，需要将owner排除后返回member列表
     * @param maxAffiliationsSize affiliations 最大长度
     * @return 过滤后的liveRoomInfo
     */
    public LiveRoomInfo filterLiveRoomInfo(int maxAffiliationsSize) {

        if (affiliationsCount <= maxAffiliationsSize) {
            if (affiliations.parallelStream().anyMatch(a -> a.containsKey(OWNER))) {
                // affiliations移除owner
                affiliations = affiliations.parallelStream().filter(a -> !a.containsKey(OWNER))
                        .collect(Collectors.toList());
                affiliationsCount = affiliationsCount - 1;
            }
            return this;
        }

        if (affiliations.parallelStream()
                .limit(maxAffiliationsSize)
                .anyMatch(a -> a.containsKey(OWNER))) {

            affiliations = affiliations.parallelStream()
                    .limit(maxAffiliationsSize + 1)
                    .filter(a -> !a.containsKey(OWNER))
                    .collect(Collectors.toList());
        }
        else {
            affiliations = affiliations.parallelStream()
                    .limit(maxAffiliationsSize)
                    .collect(Collectors.toList());
        }
        return this;
    }
}
