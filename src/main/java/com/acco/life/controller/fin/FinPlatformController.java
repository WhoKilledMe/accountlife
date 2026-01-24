package com.acco.life.controller.fin;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinPlatformDto;
import com.acco.life.service.fin.FinPlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 平台字典管理 Controller
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/fin/platform")
@RequiredArgsConstructor
public class FinPlatformController {

    private final FinPlatformService platformService;

    /**
     * 创建平台
     */
    @PostMapping
    public Mono<ResponseEntity<FinPlatformDto>> create(@RequestBody FinPlatformDto dto) {
        return platformService.create(dto)
                .map(ResponseEntity::ok);
    }

    /**
     * 更新平台
     */
    @PutMapping
    public Mono<ResponseEntity<FinPlatformDto>> update(@RequestBody FinPlatformDto dto) {
        return platformService.update(dto)
                .map(ResponseEntity::ok);
    }

    /**
     * 根据ID查询平台
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<FinPlatformDto>> findById(@PathVariable Long id) {
        return platformService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 根据平台代码查询
     */
    @GetMapping("/code/{platformCode}")
    public Mono<ResponseEntity<FinPlatformDto>> findByPlatformCode(@PathVariable String platformCode) {
        return platformService.findByPlatformCode(platformCode)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 查询所有启用的平台
     */
    @GetMapping("/active")
    public Mono<ResponseEntity<List<FinPlatformDto>>> findAllActive() {
        return platformService.findAllActive()
                .map(ResponseEntity::ok);
    }

    /**
     * 查询所有平台
     */
    @GetMapping
    public Mono<ResponseEntity<List<FinPlatformDto>>> findAll() {
        return platformService.findAll()
                .map(ResponseEntity::ok);
    }

    /**
     * 根据平台类型查询
     */
    @GetMapping("/type/{platformType}")
    public Mono<ResponseEntity<List<FinPlatformDto>>> findByPlatformType(@PathVariable String platformType) {
        return platformService.findByPlatformType(platformType)
                .map(ResponseEntity::ok);
    }

    /**
     * 分页查询
     */
    @PostMapping("/page")
    public Mono<ResponseEntity<PageResponse<FinPlatformDto>>> page(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String platformType,
            @RequestParam(required = false) String status) {
        return platformService.page(platformType, status, page, size)
                .map(ResponseEntity::ok);
    }

    /**
     * 删除平台（软删除）
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable Long id) {
        return platformService.deleteById(id)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }
}
