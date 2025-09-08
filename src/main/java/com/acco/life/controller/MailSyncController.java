package com.acco.life.controller;

import com.acco.life.dto.MailSyncRequestDto;
import com.acco.life.dto.UserMailConfigDto;
import com.acco.life.service.UserMailConfigService;
import com.acco.life.util.MailUtil;
import com.acco.life.util.PythonScriptManager;
import com.acco.life.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/mailsync")
@RequiredArgsConstructor
@Slf4j
public class MailSyncController {

    private final UserMailConfigService userMailConfigService;
    private final PythonScriptManager pythonScriptManager;

    @PostMapping("/email")
    public Mono<ResponseEntity<String>> syncByEmail(@RequestBody MailSyncRequestDto req) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> {
                    UserMailConfigDto filter = new UserMailConfigDto();
                    filter.setUserId(userId);
                    return userMailConfigService.query(filter);
                })
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(Flux.error(new IllegalStateException("未找到用户邮箱配置")))
                .flatMap(cfg -> Mono.fromCallable(() -> doSyncWithMailUtil(cfg, req)))
                .collectList()
                .map(results -> ResponseEntity.ok(String.join("\n\n", results)))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    private String doSyncWithMailUtil(UserMailConfigDto cfg, MailSyncRequestDto req) {
        String host = cfg.getHost();
        String port = cfg.getPort() == null ? null : String.valueOf(cfg.getPort());
        String username = cfg.getEmailAddress();
        String password = cfg.getAuthCode();
        String date = req.getMailDate();
        String destDir = "/Users/wensenzhang/workspaces/email/" + username;
        // Use search+download by IMAP terms; if only one date provided, set both start and end
        return MailUtil.searchAndDownloadAttachmentsBySender(host, port, username, password, req.getMailSender(), date, date, destDir);
    }
}


