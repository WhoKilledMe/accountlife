package com.acco.life.factory;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionNingBoBank;
import com.acco.life.enums.TransactionSourceType;

import java.util.HashMap;
import java.util.Map;

/**
 * description: [此处简要描述文件功能]
 *
 * @author wensen.zhang
 * @version V1.0.0
 * @date: 2025-07-22 20:02:27
 */
public class FileTransactionFactory {

    public static final Map<TransactionSourceType, Class<? extends FileTransactionDto>> fileTransactionMap = new HashMap<>();

    static {
        fileTransactionMap.put(TransactionSourceType.NING_BO_CREDIT, FileTransactionNingBoBank.class);
    }

}
