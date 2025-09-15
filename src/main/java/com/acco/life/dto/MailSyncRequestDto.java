package com.acco.life.dto;

import lombok.Data;

@Data
public class MailSyncRequestDto {

    private String mailDate;

    private String mailSender;

    private Long accountId;

    private String accountName;

    private String zipPassword;
}


