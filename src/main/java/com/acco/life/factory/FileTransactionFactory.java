package com.acco.life.factory;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionNingBoBank;
import com.acco.life.enums.TransactionSourceType;

import java.util.HashMap;
import java.util.Map;

/**
 * description: 文件交易工厂类，根据交易来源类型生成对应的交易DTO类型
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public class FileTransactionFactory {

    public static final Map<TransactionSourceType, Class<? extends FileTransactionDto>> fileTransactionMap = new HashMap<>();

    static {
        fileTransactionMap.put(TransactionSourceType.NING_BO_CREDIT, FileTransactionNingBoBank.class);
    }

}
