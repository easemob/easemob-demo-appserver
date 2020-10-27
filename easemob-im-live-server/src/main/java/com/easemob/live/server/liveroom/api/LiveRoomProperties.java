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

    /**
     * Max size for affiliations returned.
     */
    private Integer maxAffiliationsSize = 200;

    /**
     * ping stream status delay milliseconds
     */
    private Integer streamPingDelay = 60000;

    /**
     * liveroom cleanup delay milliseconds
     */
    private Integer liveroomCleanDelay = 3600000;
}
