package com.acco.life.controller;

import com.acco.life.dto.AccountTransactionUploadFileLogDto;
import com.acco.life.dto.MailSyncRequestDto;
import com.acco.life.dto.MailSearchConfigDto;
import com.acco.life.dto.UserMailConfigDto;
import com.acco.life.enums.UploadLogStatus;
import com.acco.life.service.AccountTransactionUploadFileLogService;
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

import java.util.List;

@RestController
@RequestMapping("/api/mailsync")
@RequiredArgsConstructor
@Slf4j
public class MailSyncController {

    private final UserMailConfigService userMailConfigService;
    private final PythonScriptManager pythonScriptManager; // reserved for future use
    private final AccountTransactionUploadFileLogService accountTransactionUploadFileLogService;

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
                .flatMap(cfg -> Mono.fromCallable(() -> doSyncWithMailUtilDetailed(cfg, req))
                        .flatMapMany(Flux::fromIterable)
                        .flatMap(att -> saveSyncLogWithFile(cfg, req, att))
                        .map(dto -> dto.getFileName() + "|" + dto.getFilePath() + "|" + (dto.getMd5Checksum() == null ? "" : dto.getMd5Checksum())))
                .collectList()
                .map(list -> ResponseEntity.ok(String.join("\n", list)))
                .onErrorResume(err -> Mono.just(ResponseEntity.badRequest().body(err.getMessage())));
    }

    private String doSyncWithMailUtil(UserMailConfigDto cfg, MailSyncRequestDto req) {
        MailSearchConfigDto config = new MailSearchConfigDto(
                cfg.getHost(),
                cfg.getPort() == null ? null : String.valueOf(cfg.getPort()),
                cfg.getEmailAddress(),
                cfg.getAuthCode(),
                req.getMailSender(),
                req.getMailDate(),
                req.getMailDate(),
                "/Users/wensenzhang/workspaces/email/" + cfg.getEmailAddress()
        );
        return MailUtil.searchAndDownloadAttachmentsBySender(config);
    }

    private List<MailUtil.DownloadedAttachment> doSyncWithMailUtilDetailed(UserMailConfigDto cfg, MailSyncRequestDto req) {
        MailSearchConfigDto config = new MailSearchConfigDto(
                cfg.getHost(),
                cfg.getPort() == null ? null : String.valueOf(cfg.getPort()),
                cfg.getEmailAddress(),
                cfg.getAuthCode(),
                req.getMailSender(),
                req.getMailDate(),
                req.getMailDate(),
                "/Users/wensenzhang/workspaces/email/" + cfg.getEmailAddress()
        );
        return MailUtil.searchAndDownloadAttachmentsDetailed(config);
    }

    private Mono<AccountTransactionUploadFileLogDto> saveSyncLogWithFile(UserMailConfigDto cfg, MailSyncRequestDto req, MailUtil.DownloadedAttachment att) {
        AccountTransactionUploadFileLogDto dto = new AccountTransactionUploadFileLogDto();
        dto.setUserId(cfg.getUserId());
        dto.setAccountId(req.getAccountId());
        dto.setStatus(UploadLogStatus.PENDING.getCode());
        dto.setFileName(att.fileName());
        dto.setFilePath(att.filePath());
        dto.setMd5Checksum(att.md5Checksum());
        try {
            if (req.getZipPassword() != null && !req.getZipPassword().isEmpty()) {
                dto.setZipPassword(Integer.valueOf(req.getZipPassword()));
            }
        } catch (NumberFormatException ignored) {
        }
        return accountTransactionUploadFileLogService.save(Mono.just(dto));
    }
}


