package com.easemob.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaRepositories
@EnableDiscoveryClient
@EnableScheduling
@EntityScan("com.easemob.app.model")
@SpringBootApplication(scanBasePackages = "com.easemob")
@EnableFeignClients(basePackages = "com.easemob")
public class AppServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppServerApplication.class, args);
    }
}
