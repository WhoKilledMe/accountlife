package com.acco.life.controller;

import com.acco.life.dto.UserMailConfigDto;
import com.acco.life.service.AuthService;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserMailConfigControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuthService authService;

    @Test
    void queryUser3AndPrintMailSubjectsAndDates() throws Exception {
        UserMailConfigDto filter = new UserMailConfigDto();
        filter.setUserId(3L);

        // 1) 查询 userId=3 的所有邮箱配置
        when(authService.getUserIdByToken("testtoken")).thenReturn(Mono.just(3L));

        UserMailConfigDto[] configs = webTestClient.post()
                .uri("/api/usermailconfig/query")
                .header("Authorization", "Bearer testtoken")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(filter), UserMailConfigDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserMailConfigDto[].class)
                .returnResult()
                .getResponseBody();

        if (configs == null || configs.length == 0) {
            System.out.println("[单测] userId=3 未查询到邮箱配置");
            return;
        }

        for (UserMailConfigDto cfg : configs) {
            System.out.println("===== 测试配置: " + Optional.ofNullable(cfg.getName()).orElse("<未命名>") +
                    " | host=" + cfg.getHost() + ":" + cfg.getPort() +
                    " | account=" + cfg.getEmailAddress());

            // 2) 测试连通性 /api/usermailconfig/test
            Boolean ok = webTestClient.post()
                    .uri("/api/usermailconfig/test")
                    .header("Authorization", "Bearer testtoken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(cfg), UserMailConfigDto.class)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Boolean.class)
                    .returnResult()
                    .getResponseBody();

            System.out.println("[连通性] " + (Boolean.TRUE.equals(ok) ? "OK" : "FAIL"));

            // 3) 实际连接IMAP，读取最新若干封邮件主题与日期，便于排查
            try {
                printLatestSubjectsAndDates(cfg, 400);
            } catch (Exception e) {
                System.out.println("[读取邮件异常] " + e.getMessage());
            }
        }
    }

    private void printLatestSubjectsAndDates(UserMailConfigDto cfg, int maxCount) throws Exception {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.imaps.host", Objects.toString(cfg.getHost(), ""));
        if (cfg.getPort() != null) {
            props.setProperty("mail.imaps.port", String.valueOf(cfg.getPort()));
        }
        props.put("mail.imaps.ssl.enable", String.valueOf(Boolean.TRUE.equals(cfg.getEnableSsl()) || true));
        props.put("mail.imaps.ssl.trust", "*");
        if (cfg.getConnectionTimeout() != null) {
            props.put("mail.imaps.connectiontimeout", String.valueOf(cfg.getConnectionTimeout()));
        }
        if (cfg.getReadTimeout() != null) {
            props.put("mail.imaps.timeout", String.valueOf(cfg.getReadTimeout()));
        }

        Session session = Session.getInstance(props);
        Store store = session.getStore("imaps");
        store.connect(cfg.getEmailAddress(), Objects.toString(cfg.getAuthCode(), ""));
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        Message[] all = inbox.getMessages();
        if (all == null || all.length == 0) {
            System.out.println("[IMAP] 无邮件");
        } else {
            int start = Math.max(0, all.length - maxCount);
            for (int i = all.length - 1; i >= start; i--) {
                Message m = all[i];
                String subject = m.getSubject();
                Date when = m.getSentDate() != null ? m.getSentDate() : m.getReceivedDate();
                String whenStr = when == null ? "" : when.toInstant().atZone(ZoneId.systemDefault()).toString();
                System.out.println("[IMAP] subject=" + subject + " | date=" + whenStr);
            }
        }

        inbox.close(false);
        store.close();
    }
}


