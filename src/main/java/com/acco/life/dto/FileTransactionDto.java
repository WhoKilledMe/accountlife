package com.acco.life.dto;

import lombok.Data;

/**
 * description: [此处简要描述文件功能]
 *
 * @author wensen.zhang
 * @version V1.0.0
 * @date: 2025-07-22 19:45:48
 */
@Data
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = FileTransactionNingBoBank.class, name = "ningbo")})
public class FileTransactionDto {

   // private String fileType;
}
