package com.acco.life.service;

import com.acco.life.enums.TransactionSourceType;

import java.io.InputStream;

/**
 * description: [此处简要描述文件功能]
 *
 * @author wensen.zhang
 * @version V1.0.0
 * @date: 2025-07-22 20:16:00
 */
public interface TransactionCsvParseService {

    void parseCsvFile(InputStream inputStream, TransactionSourceType transactionSourceType);
}
