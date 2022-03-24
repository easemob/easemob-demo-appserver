package com.easemob.agora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ClassName: SpringBootApplication
 * description:
 * author: lijian
 * date: 2021-01-19 09:15
 **/
@EnableDiscoveryClient
@SpringBootApplication
public class AgoraServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgoraServerApplication.class, args);
    }
}
