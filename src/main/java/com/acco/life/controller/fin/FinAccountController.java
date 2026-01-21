package com.acco.life.controller.fin;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinAccountDto;
import com.acco.life.service.fin.FinAccountService;
import com.acco.life.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 账户管理 Controller
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/fin/account")
@RequiredArgsConstructor
public class FinAccountController {

    private final FinAccountService accountService;

    /**
     * 创建账户
     */
    @PostMapping
    public Mono<ResponseEntity<FinAccountDto>> create(@RequestBody FinAccountDto dto) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> {
                    dto.setUserId(userId);
                    return accountService.create(dto);
                })
                .map(ResponseEntity::ok);
    }

    /**
     * 更新账户
     */
    @PutMapping
    public Mono<ResponseEntity<FinAccountDto>> update(@RequestBody FinAccountDto dto) {
        return accountService.update(dto)
                .map(ResponseEntity::ok);
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<FinAccountDto>> findById(@PathVariable Long id) {
        return accountService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 查询当前用户所有账户
     */
    @GetMapping
    public Mono<ResponseEntity<List<FinAccountDto>>> findAll() {
        return UserUtil.getCurrentUserId()
                .flatMap(accountService::findByUserId)
                .map(ResponseEntity::ok);
    }

    /**
     * 分页查询
     */
    @PostMapping("/page")
    public Mono<ResponseEntity<PageResponse<FinAccountDto>>> page(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String platformCode,
            @RequestParam(required = false) String accountCategory,
            @RequestParam(required = false) String status) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> accountService.page(userId, platformCode, accountCategory, status, page, size))
                .map(ResponseEntity::ok);
    }

    /**
     * 删除账户
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable Long id) {
        return accountService.deleteById(id)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    /**
     * 根据平台代码查询账户
     */
    @GetMapping("/platform/{platformCode}")
    public Mono<ResponseEntity<List<FinAccountDto>>> findByPlatformCode(@PathVariable String platformCode) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> accountService.findByUserIdAndPlatformCode(userId, platformCode))
                .map(ResponseEntity::ok);
    }
}
