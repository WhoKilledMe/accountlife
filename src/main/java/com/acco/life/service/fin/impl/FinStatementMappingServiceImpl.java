package com.acco.life.service.fin.impl;

import com.acco.life.entity.fin.FinStatementMappingRule;
import com.acco.life.repository.fin.FinStatementMappingRuleRepository;
import com.acco.life.service.fin.FinStatementMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
        return ruleRepository.findByPlatformCodeAndSourceTypeAndEnabled(platformCode, sourceType, true)
                .collectList()
                .map(list -> list.stream()
                        .sorted(Comparator.comparingInt(rule -> rule.getPriority() != null ? -rule.getPriority() : 0))
                        .collect(Collectors.toList()))
                .doOnNext(list -> cache.put(cacheKey, list));
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

