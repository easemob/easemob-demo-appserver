package com.easemob.discord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.easemob")
public class DiscordServerStarter {
    public static void main(String[] args) {
        SpringApplication.run(DiscordServerStarter.class, args);
    }
}
