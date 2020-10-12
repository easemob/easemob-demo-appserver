package com.easemob.live.server.liveroom.api;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author shenchong@easemob.com 2020/3/3
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "easemob.live.limit")
public class LiveRoomProperties {

    /**
     * Max size for list returned.
     */
    private Integer batchMaxSize = 100;
}
