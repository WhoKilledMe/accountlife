package com.acco.life.controller;

import com.acco.life.dto.UserGroupDto;
import com.acco.life.service.UserGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "UserGroup 接口")
@RestController
@RequestMapping("/api/usergroup")
@RequiredArgsConstructor
public class UserGroupController {

    private final UserGroupService service;

    @Operation(summary = "查询所有 UserGroup")
    @GetMapping
    public Flux<UserGroupDto> list() {
        return service.findAll();
    }

    @Operation(summary = "根据 ID 查询 UserGroup")
    @GetMapping("/{id}")
    public Mono<UserGroupDto> get(@PathVariable Integer id) {
        return service.findById(id);
    }

    @Operation(summary = "创建 UserGroup")
    @PostMapping
    public Mono<UserGroupDto> create(@RequestBody Mono<UserGroupDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "更新 UserGroup")
    @PutMapping
    public Mono<UserGroupDto> update(@RequestBody Mono<UserGroupDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "删除 UserGroup")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return service.deleteById(id);
    }
}
