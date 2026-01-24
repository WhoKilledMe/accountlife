package com.acco.life.factory;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionLabelDetail;
import com.acco.life.dto.FileTransactionNingBoBank;
import com.acco.life.dto.FileTransactionMeituan;
import com.acco.life.dto.FileTransactionAlipay;
import com.acco.life.dto.FileTransactionWechat;
import com.acco.life.dto.FileTransactionJingdong;
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
        fileTransactionMap.put(TransactionSourceType.LABEL_DETAIL, FileTransactionLabelDetail.class);
        fileTransactionMap.put(TransactionSourceType.PLATFORM, FileTransactionMeituan.class);
        fileTransactionMap.put(TransactionSourceType.ALIPAY, FileTransactionAlipay.class);
        fileTransactionMap.put(TransactionSourceType.WECHAT, FileTransactionWechat.class);
        fileTransactionMap.put(TransactionSourceType.JINGDONG, FileTransactionJingdong.class);
    }

}
