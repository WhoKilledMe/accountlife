package com.acco.life.controller;

import com.acco.life.dto.UserGroupDto;
import com.acco.life.service.UserGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 用户组控制器，提供用户组的增删改查接口
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Tag(name = "UserGroup 接口")
@RestController
@RequestMapping("/api/usergroup")
@RequiredArgsConstructor
public class UserGroupController {

    private final UserGroupService service;

    @Operation(summary = "查询所有 UserGroup")
    @GetMapping
    public Mono<ResponseEntity<List<UserGroupDto>>> list() {
        return service.findAll().map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "根据 ID 查询 UserGroup")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserGroupDto>> get(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok)
        .onErrorResume(e ->
        Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "创建 UserGroup")
    @PostMapping
    public Mono<ResponseEntity<UserGroupDto>> create(@RequestBody Mono<UserGroupDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                .onErrorResume(e ->
                Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "更新 UserGroup")
    @PutMapping
    public Mono<ResponseEntity<UserGroupDto>> update(@RequestBody Mono<UserGroupDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "删除 UserGroup")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return service.deleteById(id);
    }
}
