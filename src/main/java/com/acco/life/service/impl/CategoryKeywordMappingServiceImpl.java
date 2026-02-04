package com.acco.life.service.impl;

import com.acco.life.entity.CategoryKeywordMapping;
import com.acco.life.repository.CategoryKeywordMappingRepository;
import com.acco.life.service.CategoryKeywordMappingService;
import com.acco.life.util.TextSegmentUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryKeywordMappingServiceImpl implements CategoryKeywordMappingService {
    
    private final CategoryKeywordMappingRepository repository;
    
    /**
     * 关键词映射缓存
     * Key: keyword.toLowerCase()
     * Value: List<CategoryKeywordMapping> 按优先级排序（用户自定义 > 系统，权重高 > 权重低）
     */
    private final Map<String, List<CategoryKeywordMapping>> keywordCache = new ConcurrentHashMap<>();
    
    /**
     * 分词查询结果缓存（使用Caffeine实现LRU淘汰）
     * Key: "userId:keyword"
     * Value: categoryId
     */
    private final Cache<String, Long> categoryIdCache = Caffeine.newBuilder()
            .maximumSize(10_000)  // 最多缓存1万条查询结果
            .expireAfterWrite(Duration.ofMinutes(30))  // 30分钟后过期
            .build();
    
    /**
     * 应用启动时预加载所有关键词到内存
     */
    @PostConstruct
    public void init() {
        log.info("开始预加载关键词映射表到内存...");
        long startTime = System.currentTimeMillis();
        
        repository.findAll()
                .filter(mapping -> mapping.getIsActive() != null && mapping.getIsActive())  // 只加载启用的关键词
                .collectList()
                .doOnNext(mappings -> {
                    log.info("从数据库加载了 {} 条关键词映射", mappings.size());
                    
                    // 按keyword分组，并按优先级排序
                    for (CategoryKeywordMapping mapping : mappings) {
                        String key = mapping.getKeyword().toLowerCase().trim();
                        keywordCache.computeIfAbsent(key, k -> new ArrayList<>()).add(mapping);
                    }
                    
                    // 对每个关键词的映射列表按优先级排序
                    keywordCache.values().forEach(list -> list.sort(Comparator
                            .comparing((CategoryKeywordMapping m) -> m.getUserId() == null ? 1 : 0)  // 用户自定义优先
                            .thenComparing(Comparator.comparing(CategoryKeywordMapping::getWeight).reversed())  // 权重高优先
                    ));
                    
                    long elapsed = System.currentTimeMillis() - startTime;
                    log.info("关键词映射表预加载完成，共 {} 个关键词，耗时 {}ms", keywordCache.size(), elapsed);
                })
                .subscribe();
    }
    
    @Override
    public Mono<Long> findCategoryIdByKeyword(String keyword, Long userId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Mono.empty();
        }
        
        String normalizedKeyword = keyword.toLowerCase().trim();
        String cacheKey = (userId != null ? userId : "system") + ":" + normalizedKeyword;
        
        // 1. 先查询二级缓存（查询结果缓存）
        Long cachedCategoryId = categoryIdCache.getIfPresent(cacheKey);
        if (cachedCategoryId != null) {
            log.debug("命中缓存：文本 '{}' -> 分类ID: {}", keyword, cachedCategoryId);
            return Mono.just(cachedCategoryId);
        }
        
        // 2. 使用分词匹配：对输入文本进行分词，然后匹配关键词表（从内存缓存中查找）
        List<String> segments = TextSegmentUtil.getSegments(normalizedKeyword);
        
        if (segments.isEmpty()) {
            return Mono.empty();
        }
        
        // 3. 对每个分词结果进行匹配（从内存keywordCache中查找，不查数据库）
        return Mono.fromCallable(() -> {
            for (String segment : segments) {  // segments已按长度降序排列
                String segmentKey = segment.toLowerCase().trim();
                List<CategoryKeywordMapping> mappings = keywordCache.get(segmentKey);
                
                if (mappings == null || mappings.isEmpty()) {
                    continue;
                }
                
                // 优先匹配用户自定义，再匹配系统（已在init时排序）
                for (CategoryKeywordMapping mapping : mappings) {
                    if (userId != null && Objects.equals(mapping.getUserId(), userId)) {
                        // 找到用户自定义的关键词
                        Long categoryId = mapping.getCategoryId();
                        categoryIdCache.put(cacheKey, categoryId);  // 缓存结果
                        log.debug("内存匹配（用户自定义）：文本 '{}' 通过分词 '{}' 匹配到分类ID: {}", keyword, segment, categoryId);
                        return categoryId;
                    }
                }
                
                // 查找系统关键词
                for (CategoryKeywordMapping mapping : mappings) {
                    if (mapping.getUserId() == null) {
                        Long categoryId = mapping.getCategoryId();
                        categoryIdCache.put(cacheKey, categoryId);  // 缓存结果
                        log.debug("内存匹配（系统）：文本 '{}' 通过分词 '{}' 匹配到分类ID: {}", keyword, segment, categoryId);
                        return categoryId;
                    }
                }
            }
            
            // 未找到匹配
            return null;
        })
        .flatMap(categoryId -> categoryId != null ? Mono.just(categoryId) : Mono.empty());
    }
    
    @Override
    public Flux<CategoryKeywordMapping> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Flux.empty();
        }
        String normalizedKeyword = keyword.toLowerCase().trim();
        return repository.findByKeywordContaining(normalizedKeyword);
    }
    
    @Override
    public Flux<CategoryKeywordMapping> findByCategoryId(Long categoryId) {
        if (categoryId == null) {
            return Flux.empty();
        }
        return repository.findByCategoryId(categoryId);
    }
    
    @Override
    public Flux<CategoryKeywordMapping> findByUserId(Long userId) {
        if (userId == null) {
            return Flux.empty();
        }
        return repository.findByUserId(userId);
    }
    
    @Override
    public Mono<CategoryKeywordMapping> addKeywordMapping(CategoryKeywordMapping mapping) {
        if (mapping == null) {
            return Mono.error(new IllegalArgumentException("映射不能为空"));
        }
        if (mapping.getWeight() == null) {
            mapping.setWeight(1);
        }
        if (mapping.getIsActive() == null) {
            mapping.setIsActive(true);
        }
        return repository.save(mapping);
    }
    
    @Override
    public Mono<CategoryKeywordMapping> updateKeywordMapping(CategoryKeywordMapping mapping) {
        if (mapping == null || mapping.getId() == null) {
            return Mono.error(new IllegalArgumentException("映射ID不能为空"));
        }
        Long id = Objects.requireNonNull(mapping.getId(), "id不能为空");
        return repository.findById(id)
                .flatMap(existing -> {
                    CategoryKeywordMapping nonNullExisting = Objects.requireNonNull(existing, "existing不能为空");
                    if (mapping.getCategoryId() != null) existing.setCategoryId(mapping.getCategoryId());
                    if (mapping.getKeyword() != null) existing.setKeyword(mapping.getKeyword());
                    if (mapping.getWeight() != null) existing.setWeight(mapping.getWeight());
                    if (mapping.getUserId() != null) existing.setUserId(mapping.getUserId());
                    if (mapping.getIsActive() != null) existing.setIsActive(mapping.getIsActive());
                    return repository.save(nonNullExisting);
                });
    }
    
    @Override
    public Mono<Void> deleteKeywordMapping(Long id) {
        if (id == null) {
            return Mono.error(new IllegalArgumentException("ID不能为空"));
        }
        return repository.deleteById(id);
    }
    
    @Override
    public Flux<CategoryKeywordMapping> batchAddKeywordMappings(Flux<CategoryKeywordMapping> mappings) {
        return mappings.flatMap(this::addKeywordMapping);
    }
    
    @Override
    public Flux<String> getAllActiveKeywords() {
        return repository.findAllActiveKeywords();
    }
    
    @Override
    public Flux<CategoryKeywordMapping> findByWeightGreaterThanEqual(Integer minWeight) {
        if (minWeight == null) {
            return Flux.empty();
        }
        return repository.findByWeightGreaterThanEqual(minWeight);
    }
} 