package com.acco.life.controller;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.UserMailConfigDto;
import com.acco.life.service.UserMailConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import com.acco.life.util.UserUtil;

@Tag(name = "UserMailConfig 接口")
@RestController
@RequestMapping("/api/usermailconfig")
@RequiredArgsConstructor
public class UserMailConfigController {

    private static final Logger log = LoggerFactory.getLogger(UserMailConfigController.class);
    private final UserMailConfigService service;

    @Operation(summary = "查询所有 用户邮箱配置")
    @GetMapping
    public Mono<ResponseEntity<List<UserMailConfigDto>>> list() {
        return service.findAll().map(ResponseEntity::ok)
                .onErrorResume(e -> { log.error("list mail configs failed", e); return Mono.just(ResponseEntity.badRequest().build()); });
    }

    @Operation(summary = "分页查询 用户邮箱配置")
    @PostMapping("/page")
    public Mono<ResponseEntity<PageResponse<UserMailConfigDto>>> page(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestBody(required = false) UserMailConfigDto filter
    ) {
        return service.page(filter, page, size)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> { log.error("page mail configs failed", e); return Mono.just(ResponseEntity.badRequest().build()); });
    }

    @Operation(summary = "按过滤条件查询 用户邮箱配置（内置用户ID）")
    @PostMapping("/query")
    public Mono<ResponseEntity<List<UserMailConfigDto>>> query(@RequestBody(required = false) UserMailConfigDto filter) {
        return service.query(filter)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> { log.error("query mail configs failed", e); return Mono.just(ResponseEntity.badRequest().build()); });
    }

    @Operation(summary = "根据ID查询 用户邮箱配置")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserMailConfigDto>> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok)
                .onErrorResume(e -> { log.error("get mail config failed, id={}", id, e); return Mono.just(ResponseEntity.badRequest().build()); });
    }

    @Operation(summary = "创建 用户邮箱配置")
    @PostMapping
    public Mono<ResponseEntity<UserMailConfigDto>> create(@RequestBody Mono<UserMailConfigDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                .onErrorResume(e -> { log.error("create mail config failed", e); return Mono.just(ResponseEntity.badRequest().build()); });
    }

    @Operation(summary = "更新 用户邮箱配置")
    @PutMapping
    public Mono<ResponseEntity<UserMailConfigDto>> update(@RequestBody Mono<UserMailConfigDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                .onErrorResume(e -> { log.error("update mail config failed", e); return Mono.just(ResponseEntity.badRequest().build()); });
    }

    @Operation(summary = "删除 用户邮箱配置")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return service.deleteById(id);
    }

    @Operation(summary = "测试邮箱配置连通性")
    @PostMapping("/test")
    public Mono<ResponseEntity<Boolean>> test(@RequestBody UserMailConfigDto dto) {
        return service.testConnectivity(dto)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> { log.error("test mail config failed", e); return Mono.just(ResponseEntity.badRequest().build()); });
    }

    @Operation(summary = "根据当前登录用户查询单条邮箱配置")
    @GetMapping("/me")
    public Mono<ResponseEntity<UserMailConfigDto>> findMyMailConfig() {
        return UserUtil.getCurrentUserId()
                .flatMap(service::findOneByUserId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()))
                .onErrorResume(e -> { log.error("find my mail config failed", e); return Mono.just(ResponseEntity.badRequest().build()); });
    }
}


