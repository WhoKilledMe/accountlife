package com.acco.life.controller;

import com.acco.life.dto.UserGroupMemberDto;
import com.acco.life.service.UserGroupMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "UserGroupMember 接口")
@RestController
@RequestMapping("/api/usergroupmember")
@RequiredArgsConstructor
public class UserGroupMemberController {

    private final UserGroupMemberService service;

    @Operation(summary = "查询所有 UserGroupMember")
    @GetMapping
    public Flux<UserGroupMemberDto> list() {
        return service.findAll();
    }

    @Operation(summary = "根据 ID 查询 UserGroupMember")
    @GetMapping("/{id}")
    public Mono<UserGroupMemberDto> get(@PathVariable Integer id) {
        return service.findById(id);
    }

    @Operation(summary = "创建 UserGroupMember")
    @PostMapping
    public Mono<UserGroupMemberDto> create(@RequestBody Mono<UserGroupMemberDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "更新 UserGroupMember")
    @PutMapping
    public Mono<UserGroupMemberDto> update(@RequestBody Mono<UserGroupMemberDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "删除 UserGroupMember")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return service.deleteById(id);
    }
}
