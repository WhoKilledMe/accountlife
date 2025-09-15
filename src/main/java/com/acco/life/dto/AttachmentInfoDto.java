package com.acco.life.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentInfoDto {

    private String fileName;

    private String filePath;

    private String md5Checksum;
}


