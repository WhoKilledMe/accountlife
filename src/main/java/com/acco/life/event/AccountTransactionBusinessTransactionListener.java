package com.acco.life.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * description: [此处简要描述文件功能]
 *
 * @author wensen.zhang
 * @version V1.0.0
 * @date: 2025-09-23 11:55:12
 */
@Component
public class AccountTransactionBusinessTransactionListener {

    @EventListener
    public void handleAccountTransactionEvent(AccountTransactionEvent event) {

        List<Long> transactionIds = event.getTransactionIds();
    }
}
