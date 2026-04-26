package com.loveai.chat;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class ChatAiApplication {
    public static void main(String[] args) {
        log.info("启动");
        SpringApplication.run(ChatAiApplication.class, args);
        System.out.println("项目启动成功");
    }

}
