package com.acco.life.dto;

import lombok.Data;

@Data
public class MailSyncRequestDto {
    private String mailDate; // yyyy-MM-dd
    private String mailSubject;
    private String mailSender;
    private Long accountId;
    private String accountName;
}


