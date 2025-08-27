package com.acco.life.controller;

import com.acco.life.common.ApiResponse;
import com.acco.life.entity.CategoryKeywordMapping;
import com.acco.life.service.CategoryKeywordMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/category-keywords")
@RequiredArgsConstructor
public class CategoryKeywordMappingController {
    
    private final CategoryKeywordMappingService service;
    
    /**
     * 根据关键词智能匹配分类ID（优先用户自定义）
     */
    @GetMapping("/match")
    public Mono<ApiResponse<Integer>> matchCategoryId(@RequestParam String keyword,
                                                      @RequestParam(required = false) Integer userId) {
        return service.findCategoryIdByKeyword(keyword, userId)
                .map(ApiResponse::success)
                .switchIfEmpty(Mono.just(ApiResponse.success(null)));
    }
    
    /**
     * 根据关键词搜索映射
     */
    @GetMapping("/search")
    public Mono<ApiResponse<Flux<CategoryKeywordMapping>>> searchByKeyword(@RequestParam String keyword) {
        Flux<CategoryKeywordMapping> results = service.searchByKeyword(keyword);
        return Mono.just(ApiResponse.success(results));
    }
    
    /**
     * 根据分类ID查找关键词
     */
    @GetMapping("/by-category-id")
    public Mono<ApiResponse<Flux<CategoryKeywordMapping>>> findByCategoryId(@RequestParam Integer categoryId) {
        Flux<CategoryKeywordMapping> results = service.findByCategoryId(categoryId);
        return Mono.just(ApiResponse.success(results));
    }
    
    /**
     * 根据用户ID查找自定义关键词
     */
    @GetMapping("/by-user-id")
    public Mono<ApiResponse<Flux<CategoryKeywordMapping>>> findByUserId(@RequestParam Integer userId) {
        Flux<CategoryKeywordMapping> results = service.findByUserId(userId);
        return Mono.just(ApiResponse.success(results));
    }
    
    @PostMapping
    public Mono<ApiResponse<CategoryKeywordMapping>> addMapping(@RequestBody CategoryKeywordMapping mapping) {
        return service.addKeywordMapping(mapping)
                .map(ApiResponse::success)
                .onErrorResume(e -> Mono.just(ApiResponse.error(e.getMessage())));
    }
    
    @PutMapping("/{id}")
    public Mono<ApiResponse<CategoryKeywordMapping>> updateMapping(@PathVariable Integer id,
                                                                   @RequestBody CategoryKeywordMapping mapping) {
        mapping.setId(id);
        return service.updateKeywordMapping(mapping)
                .map(ApiResponse::success)
                .onErrorResume(e -> Mono.just(ApiResponse.error(e.getMessage())));
    }
    
    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> deleteMapping(@PathVariable Integer id) {
        return service.deleteKeywordMapping(id)
                .then(Mono.just(ApiResponse.<Void>success(null)))
                .onErrorResume(e -> Mono.just(ApiResponse.<Void>error(e.getMessage())));
    }
    
    @GetMapping("/keywords")
    public Mono<ApiResponse<Flux<String>>> getAllKeywords() {
        Flux<String> keywords = service.getAllActiveKeywords();
        return Mono.just(ApiResponse.success(keywords));
    }
} 