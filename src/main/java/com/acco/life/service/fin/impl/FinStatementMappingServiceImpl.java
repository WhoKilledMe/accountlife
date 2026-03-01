package com.acco.life.service.fin.impl;

import com.acco.life.entity.fin.FinStatementMappingRule;
import com.acco.life.repository.fin.FinStatementMappingRuleRepository;
import com.acco.life.service.fin.FinStatementMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 账单映射规则服务实现
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinStatementMappingServiceImpl implements FinStatementMappingService {

    private final FinStatementMappingRuleRepository ruleRepository;

    /**
     * 简单内存缓存：key = platformCode + ":" + sourceType
     */
    private final Map<String, List<FinStatementMappingRule>> cache = new ConcurrentHashMap<>();

    /**
     * 应用启动时预加载所有映射规则到内存
     */
    @PostConstruct
    public void init() {
        log.info("开始预加载账单映射规则到内存...");
        long startTime = System.currentTimeMillis();
        
        try {
            List<FinStatementMappingRule> rules = ruleRepository.findAll()
                    .filter(rule -> rule.getEnabled() != null && rule.getEnabled())  // 只加载启用的规则
                    .collectList()
                    .block();  // 同步等待，确保启动时数据完全加载
            
            if (rules == null || rules.isEmpty()) {
                log.warn("未找到启用的映射规则");
                return;
            }
            
            log.info("从数据库加载了 {} 条映射规则", rules.size());
            
            // 按 platformCode + ":" + sourceType 分组
            Map<String, List<FinStatementMappingRule>> grouped = rules.stream()
                    .collect(Collectors.groupingBy(rule -> {
                        String platform = rule.getPlatformCode() != null ? rule.getPlatformCode() : "";
                        String sourceType = rule.getSourceType() != null ? rule.getSourceType() : "";
                        return platform + ":" + sourceType;
                    }));
            
            // 对每个分组的规则按优先级排序（优先级高的在前）
            grouped.forEach((key, ruleList) -> {
                ruleList.sort(Comparator.comparingInt(rule -> 
                        rule.getPriority() != null ? -rule.getPriority() : 0));
                cache.put(key, ruleList);
            });
            
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("账单映射规则预加载完成，共 {} 个分组，耗时 {}ms", cache.size(), elapsed);
        } catch (Exception e) {
            log.error("预加载账单映射规则失败", e);
        }
    }

    @Override
    public Mono<String> mapAccountRef(String platformCode, String rawPaymentMethod) {
        if (rawPaymentMethod == null || rawPaymentMethod.isEmpty()) {
            return Mono.justOrEmpty(rawPaymentMethod);
        }
        String platform = platformCode != null ? platformCode : "";
        String sourceType = "PAYMENT_METHOD";
        String key = platform + ":" + sourceType;

        return getRulesFromCache(platform, sourceType, key)
                .map(rules -> applyAccountRefRules(rules, rawPaymentMethod))
                .defaultIfEmpty(rawPaymentMethod);
    }

    private Mono<List<FinStatementMappingRule>> getRulesFromCache(String platformCode, String sourceType, String cacheKey) {
        List<FinStatementMappingRule> cached = cache.get(cacheKey);
        if (cached != null) {
            return Mono.just(cached);
        }
        // 如果缓存中没有，返回空列表（数据应该在启动时已加载）
        log.debug("缓存中未找到映射规则: {}", cacheKey);
        return Mono.just(List.of());
    }

    private String applyAccountRefRules(List<FinStatementMappingRule> rules, String rawPaymentMethod) {
        String value = rawPaymentMethod;
        for (FinStatementMappingRule rule : rules) {
            if (!"ACCOUNT_REF".equals(rule.getTargetType())) {
                continue;
            }
            String pattern = rule.getSourcePattern();
            if (pattern == null || pattern.isEmpty()) {
                continue;
            }
            // 目前按包含关系匹配，后续可扩展为 LIKE/正则
            if (value.contains(pattern.replace("%", ""))) {
                return rule.getTargetValue();
            }
        }
        return value;
    }
}

