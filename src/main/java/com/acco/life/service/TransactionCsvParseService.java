package com.acco.life.service;

import com.acco.life.enums.TransactionSourceType;
import reactor.core.publisher.Mono;

import java.io.InputStream;

/**
 * description: 交易CSV解析服务接口，定义CSV文件解析与入库方法
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface TransactionCsvParseService {

    /**
     * 解析CSV文件并保存到数据库
     *
     * @param inputStream CSV文件输入流
     * @param transactionSourceType 交易来源类型
     * @param accountName 账户名称
     * @return Mono<Void> 表示操作完成
     */
    Mono<Void> parseCsvFile(InputStream inputStream, TransactionSourceType transactionSourceType, String accountName);
}