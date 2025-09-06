package com.acco.life.service.impl;

import com.acco.life.entity.CategoryKeywordMapping;
import com.acco.life.repository.CategoryKeywordMappingRepository;
import com.acco.life.service.CategoryKeywordMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryKeywordMappingServiceImpl implements CategoryKeywordMappingService {
    
    private final CategoryKeywordMappingRepository repository;
    
    @Override
    public Mono<Long> findCategoryIdByKeyword(String keyword, Long userId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Mono.empty();
        }
        String normalizedKeyword = keyword.toLowerCase().trim();
        
        // 优先匹配用户自定义，再匹配系统（user_id is null）
        return repository.findByKeywordContaining(normalizedKeyword)
                .filter(mapping -> mapping.getUserId() != null && mapping.getUserId().equals(userId))
                .sort((a, b) -> Integer.compare(b.getWeight(), a.getWeight()))
                .next()
                .switchIfEmpty(
                        repository.findByKeywordContaining(normalizedKeyword)
                                .filter(mapping -> mapping.getUserId() == null)
                                .sort((a, b) -> Integer.compare(b.getWeight(), a.getWeight()))
                                .next()
                )
                .map(CategoryKeywordMapping::getCategoryId)
                .doOnNext(id -> log.debug("关键词 '{}' 匹配到分类ID: {}", keyword, id));
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
        return repository.findById(mapping.getId())
                .flatMap(existing -> {
                    if (mapping.getCategoryId() != null) existing.setCategoryId(mapping.getCategoryId());
                    if (mapping.getKeyword() != null) existing.setKeyword(mapping.getKeyword());
                    if (mapping.getWeight() != null) existing.setWeight(mapping.getWeight());
                    if (mapping.getUserId() != null) existing.setUserId(mapping.getUserId());
                    if (mapping.getIsActive() != null) existing.setIsActive(mapping.getIsActive());
                    return repository.save(existing);
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