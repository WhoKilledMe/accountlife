package com.acco.life.repository.fin;

import com.acco.life.entity.fin.FinStatementMappingRule;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * 账单映射规则 Repository
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinStatementMappingRuleRepository extends ReactiveCrudRepository<FinStatementMappingRule, Long> {

    /**
     * 根据平台和来源字段类型查询启用的规则
     */
    Flux<FinStatementMappingRule> findByPlatformCodeAndSourceTypeAndEnabled(String platformCode, String sourceType, Boolean enabled);
}

