package com.acco.life.task;

import com.acco.life.util.MailUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * description: 定时任务，每30秒执行一次，调用MailUtil获取宁波银行邮件内容及URL返回值
 *
 * @date: 2025-07-25 15:35:00
 * @author wensen.zhang
 * @version V1.0.0
 */
@Component
public class MailCheckTask {

    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private String port;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;

    //@Scheduled(fixedRate = 30000)
    public void checkMail() {
        String result = MailUtil.fetchNingboBankMailAndUrlContent(host, port, username, password);
        System.out.println("[定时任务] 邮箱检查结果: " + result);
    }
}

