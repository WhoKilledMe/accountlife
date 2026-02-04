package com.acco.life.service.fin;

import reactor.core.publisher.Mono;

/**
 * 账单映射规则服务
 * 提供基于配置表的字段归一化能力
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinStatementMappingService {

    /**
     * 根据平台和支付方式原始文本映射为统一的 account_ref 线索
     *
     * @param platformCode 平台代码，如 JD/WECHAT/ALIPAY 等
     * @param rawPaymentMethod 原始支付方式文本，如 微信支付/余额/京东白条/京东小金库/招商银行储蓄卡(6527)
     * @return 归一化后的 account_ref（如 WECHAT_PAY/JD_BALANCE/JD_BAITIAO/JD_PAY/卡号尾号等），若无匹配则返回原始值
     */
    Mono<String> mapAccountRef(String platformCode, String rawPaymentMethod);
}

