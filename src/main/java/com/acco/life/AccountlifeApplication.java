package com.acco.life;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * description: 个人资产管理系统主应用启动类
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class AccountlifeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountlifeApplication.class, args);
    }

}