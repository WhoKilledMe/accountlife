package com.acco.life.controller.fin;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinPaymentRouteDto;
import com.acco.life.dto.fin.FinTransactionDto;
import com.acco.life.service.fin.FinTransactionService;
import com.acco.life.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易中枢 Controller
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/fin/transaction")
@RequiredArgsConstructor
public class FinTransactionController {

    private final FinTransactionService transactionService;

    /**
     * 创建交易
     */
    @PostMapping
    public Mono<ResponseEntity<FinTransactionDto>> create(@RequestBody FinTransactionDto dto) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> {
                    dto.setUserId(userId);
                    return transactionService.create(dto);
                })
                .map(ResponseEntity::ok);
    }

    /**
     * 更新交易
     */
    @PutMapping
    public Mono<ResponseEntity<FinTransactionDto>> update(@RequestBody FinTransactionDto dto) {
        return transactionService.update(dto)
                .map(ResponseEntity::ok);
    }

    /**
     * 根据ID查询（包含支付路由）
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<FinTransactionDto>> findById(@PathVariable Long id) {
        return transactionService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 根据交易号查询
     */
    @GetMapping("/no/{transactionNo}")
    public Mono<ResponseEntity<FinTransactionDto>> findByTransactionNo(@PathVariable String transactionNo) {
        return transactionService.findByTransactionNo(transactionNo)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 根据时间范围查询
     */
    @GetMapping("/range")
    public Mono<ResponseEntity<List<FinTransactionDto>>> findByTimeRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> transactionService.findByUserIdAndTimeRange(userId, startTime, endTime))
                .map(ResponseEntity::ok);
    }

    /**
     * 分页查询
     */
    @PostMapping("/page")
    public Mono<ResponseEntity<PageResponse<FinTransactionDto>>> page(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String platformCode,
            @RequestParam(required = false) Long categoryId) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> transactionService.page(userId, bizType, status, platformCode, categoryId, page, size))
                .map(ResponseEntity::ok);
    }

    /**
     * 删除交易
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable Long id) {
        return transactionService.deleteById(id)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    /**
     * 确认交易
     */
    @PostMapping("/{id}/confirm")
    public Mono<ResponseEntity<FinTransactionDto>> confirm(@PathVariable Long id) {
        return transactionService.confirmTransaction(id)
                .map(ResponseEntity::ok);
    }

    /**
     * 取消交易
     */
    @PostMapping("/{id}/cancel")
    public Mono<ResponseEntity<FinTransactionDto>> cancel(@PathVariable Long id) {
        return transactionService.cancelTransaction(id)
                .map(ResponseEntity::ok);
    }

    /**
     * 添加支付路由
     */
    @PostMapping("/{id}/route")
    public Mono<ResponseEntity<FinPaymentRouteDto>> addPaymentRoute(
            @PathVariable Long id,
            @RequestBody FinPaymentRouteDto routeDto) {
        routeDto.setTransactionId(id);
        return transactionService.addPaymentRoute(routeDto)
                .map(ResponseEntity::ok);
    }

    /**
     * 查询交易的支付路由
     */
    @GetMapping("/{id}/routes")
    public Mono<ResponseEntity<List<FinPaymentRouteDto>>> findPaymentRoutes(@PathVariable Long id) {
        return transactionService.findPaymentRoutes(id)
                .map(ResponseEntity::ok);
    }
}
