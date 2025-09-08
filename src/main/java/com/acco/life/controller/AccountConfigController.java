package com.acco.life.controller;

import com.acco.life.dto.AccountConfigDto;
import com.acco.life.common.PageResponse;
import com.acco.life.service.AccountConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 账户配置控制器
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
@Tag(name = "AccountConfig 接口")
@RestController
@RequestMapping("/api/accountconfig")
@RequiredArgsConstructor
public class AccountConfigController {
    
    private final AccountConfigService service;
    
    @Operation(summary = "查询所有启用的账户配置")
    @GetMapping
    public Mono<ResponseEntity<List<AccountConfigDto>>> list() {
        return service.getAllForSelect()
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }
    
    @Operation(summary = "根据类型查询账户配置")
    @GetMapping("/type/{type}")
    public Mono<ResponseEntity<List<AccountConfigDto>>> findByType(@PathVariable Integer type) {
        return service.findByType(type)
                .collectList()
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }
    
    @Operation(summary = "根据名称模糊查询账户配置")
    @GetMapping("/search")
    public Mono<ResponseEntity<List<AccountConfigDto>>> search(@RequestParam(required = false) String name) {
        return service.findByNameLike(name)
                .collectList()
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "分页查询系统账户配置（支持名称、类型、平台代码、是否启用筛选）")
    @PostMapping("/page")
    public Mono<ResponseEntity<PageResponse<AccountConfigDto>>> page(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestBody(required = false) AccountConfigDto filter
    ) {
        return service.page(filter, page, size)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "根据ID查询系统账户配置")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AccountConfigDto>> get(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "创建系统账户配置")
    @PostMapping
    public Mono<ResponseEntity<AccountConfigDto>> create(@RequestBody Mono<AccountConfigDto> dto) {
        return service.save(dto)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "更新系统账户配置")
    @PutMapping
    public Mono<ResponseEntity<AccountConfigDto>> update(@RequestBody Mono<AccountConfigDto> dto) {
        return service.save(dto)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "删除系统账户配置（软删除）")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return service.deleteById(id);
    }
}
