package com.easemob.live.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author shenchong@easemob.com 2020/2/12
 */
@EnableDiscoveryClient
@EnableJpaRepositories
@EntityScan("com.easemob.live.server.liveroom.model")
@SpringBootApplication(scanBasePackages = "com.easemob")
public class LiveServer {
    public static void main(String[] args) {
        SpringApplication.run(LiveServer.class, args);
    }
}
