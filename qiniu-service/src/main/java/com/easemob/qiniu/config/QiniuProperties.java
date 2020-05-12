package com.easemob.qiniu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author shenchong@easemob.com 2020/5/11
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "easemob.qiniu")
public class QiniuProperties {

    /**
     * 七牛云 Access Key
     */
    private String accessKey;

    /**
     * 七牛云 Secret Key
     */
    private String secretKey;
}
