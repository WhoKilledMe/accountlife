package com.acco.life.controller.fin;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinAccountFlowDto;
import com.acco.life.service.fin.FinAccountFlowService;
import com.acco.life.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 会计流水 Controller
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/fin/account-flow")
@RequiredArgsConstructor
public class FinAccountFlowController {

    private final FinAccountFlowService accountFlowService;

    /**
     * 创建会计流水
     */
    @PostMapping
    public Mono<ResponseEntity<FinAccountFlowDto>> create(@RequestBody FinAccountFlowDto dto) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> {
                    dto.setUserId(userId);
                    return accountFlowService.createFlow(dto);
                })
                .map(ResponseEntity::ok);
    }

    /**
     * 创建复式记账分录
     */
    @PostMapping("/double-entry")
    public Mono<ResponseEntity<List<FinAccountFlowDto>>> createDoubleEntry(
            @RequestParam Long debitAccountId,
            @RequestParam Long creditAccountId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) Long transactionId,
            @RequestParam String bizType,
            @RequestParam(required = false) String remark) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> accountFlowService.createDoubleEntry(userId, debitAccountId, creditAccountId, amount, transactionId, bizType, remark))
                .map(ResponseEntity::ok);
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<FinAccountFlowDto>> findById(@PathVariable Long id) {
        return accountFlowService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 根据账户ID查询流水
     */
    @GetMapping("/account/{accountId}")
    public Mono<ResponseEntity<List<FinAccountFlowDto>>> findByAccountId(@PathVariable Long accountId) {
        return accountFlowService.findByAccountId(accountId)
                .map(ResponseEntity::ok);
    }

    /**
     * 根据交易ID查询流水
     */
    @GetMapping("/transaction/{transactionId}")
    public Mono<ResponseEntity<List<FinAccountFlowDto>>> findByTransactionId(@PathVariable Long transactionId) {
        return accountFlowService.findByTransactionId(transactionId)
                .map(ResponseEntity::ok);
    }

    /**
     * 根据账户ID和时间范围查询
     */
    @GetMapping("/account/{accountId}/range")
    public Mono<ResponseEntity<List<FinAccountFlowDto>>> findByAccountIdAndTimeRange(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return accountFlowService.findByAccountIdAndTimeRange(accountId, startTime, endTime)
                .map(ResponseEntity::ok);
    }

    /**
     * 分页查询
     */
    @PostMapping("/page")
    public Mono<ResponseEntity<PageResponse<FinAccountFlowDto>>> page(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) String bizType) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> accountFlowService.page(userId, accountId, direction, bizType, page, size))
                .map(ResponseEntity::ok);
    }

    /**
     * 验证会计分录组的借贷平衡
     */
    @GetMapping("/journal/{journalId}/validate")
    public Mono<ResponseEntity<Boolean>> validateJournalBalance(@PathVariable Long journalId) {
        return accountFlowService.validateJournalBalance(journalId)
                .map(ResponseEntity::ok);
    }

    /**
     * 计算账户在指定时间点的余额
     */
    @GetMapping("/account/{accountId}/balance-at")
    public Mono<ResponseEntity<BigDecimal>> calculateBalanceAt(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime dateTime) {
        return accountFlowService.calculateBalanceAt(accountId, dateTime)
                .map(ResponseEntity::ok);
    }
}
