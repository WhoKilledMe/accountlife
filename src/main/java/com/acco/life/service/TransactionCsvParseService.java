package com.acco.life.service;

import com.acco.life.enums.TransactionSourceType;

import java.io.InputStream;

/**
 * description: 交易CSV解析服务接口，定义CSV文件解析相关操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface TransactionCsvParseService {

    void parseCsvFile(InputStream inputStream, TransactionSourceType transactionSourceType);
}
