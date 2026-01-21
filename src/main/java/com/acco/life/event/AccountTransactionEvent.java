package com.acco.life.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;
import java.util.List;

/**
 * description: [此处简要描述文件功能]
 *
 * @author wensen.zhang
 * @version V1.0.0
 * @date: 2025-09-23 11:45:12
 */
@Getter
@Setter
public class AccountTransactionEvent extends ApplicationEvent {

    private List<Long> transactionIds;

    public AccountTransactionEvent(Object source, Clock clock, List<Long> transactionIds) {
        super(source, clock);
        this.transactionIds = transactionIds;
    }

}
