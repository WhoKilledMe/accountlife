package com.acco.life.controller;

import com.acco.life.dto.UserDto;
import com.acco.life.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "UserAccount 接口")
@RestController
@RequestMapping("/api/useraccount")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @Operation(summary = "查询所有 UserAccount")
    @GetMapping
    public Flux<UserDto> list() {
        return service.findAll();
    }

    @Operation(summary = "根据 ID 查询 UserAccount")
    @GetMapping("/{id}")
    public Mono<UserDto> get(@PathVariable Integer id) {
        return service.findById(id);
    }

    @Operation(summary = "创建 UserAccount")
    @PostMapping
    public Mono<UserDto> create(@RequestBody Mono<UserDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "更新 UserAccount")
    @PutMapping
    public Mono<UserDto> update(@RequestBody Mono<UserDto> dto) {
        return service.save(dto);
    }

    @Operation(summary = "删除 UserAccount")
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Integer id) {
        return service.deleteById(id);
    }
}
