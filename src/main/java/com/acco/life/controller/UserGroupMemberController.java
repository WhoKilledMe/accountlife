package com.acco.life.controller;

import com.acco.life.dto.UserGroupMemberDto;
import com.acco.life.common.PageResponse;
import com.acco.life.service.UserGroupMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: 用户组成员控制器，提供用户组成员的增删改查接口
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */

@Tag(name = "UserGroupMember 接口")
@RestController
@RequestMapping("/api/usergroupmember")
@RequiredArgsConstructor
public class UserGroupMemberController {

    private final UserGroupMemberService service;

    @Operation(summary = "查询所有 UserGroupMember")
    @GetMapping
    public Mono<ResponseEntity<List<UserGroupMemberDto>>> list() {
        return service.findAll().map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "分页查询 UserGroupMember")
    @GetMapping("/page")
    public Mono<ResponseEntity<PageResponse<UserGroupMemberDto>>> page(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return service.findAll()
                .map(list -> PageResponse.fromList(list, page, size))
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "根据 ID 查询 UserGroupMember")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserGroupMemberDto>> get(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok)
        .onErrorResume(e ->
        Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "创建 UserGroupMember")
    @PostMapping
    public Mono<ResponseEntity<UserGroupMemberDto>> create(@RequestBody Mono<UserGroupMemberDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                .onErrorResume(e ->
                Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "更新 UserGroupMember")
    @PutMapping
    public Mono<ResponseEntity<UserGroupMemberDto>> update(@RequestBody Mono<UserGroupMemberDto> dto) {
        return service.save(dto).map(ResponseEntity::ok)
                    .onErrorResume(e ->
                    Mono.just(ResponseEntity.badRequest().build()));
    }

    @Operation(summary = "删除 UserGroupMember")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return service.deleteById(id);
    }
}
