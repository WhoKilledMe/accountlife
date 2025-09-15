package com.acco.life.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailSearchConfigDto {

    @NotNull
    private String host;

    @NotNull
    private String port;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String senderContains;

    @NotNull
    private String startDate;

    @NotNull
    private String endDate;

    @NotNull
    private String destDirectory;
}
