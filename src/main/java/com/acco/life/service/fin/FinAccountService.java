package com.acco.life.service.fin;

import com.acco.life.common.PageResponse;
import com.acco.life.dto.fin.FinAccountDto;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 账户管理服务接口
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinAccountService {

    /**
     * 创建账户
     */
    Mono<FinAccountDto> create(FinAccountDto dto);

    /**
     * 更新账户
     */
    Mono<FinAccountDto> update(FinAccountDto dto);

    /**
     * 根据ID查询账户
     */
    Mono<FinAccountDto> findById(Long id);

    /**
     * 根据用户ID查询所有账户
     */
    Mono<List<FinAccountDto>> findByUserId(Long userId);

    /**
     * 根据用户ID和平台代码查询
     */
    Mono<List<FinAccountDto>> findByUserIdAndPlatformCode(Long userId, String platformCode);

    /**
     * 根据用户ID、平台代码和外部账户标识查询（去重用）
     */
    Mono<FinAccountDto> findByUserIdAndPlatformCodeAndExternalRef(Long userId, String platformCode, String externalAccountRef);

    /**
     * 分页查询
     */
    Mono<PageResponse<FinAccountDto>> page(Long userId, String platformCode, String accountCategory, String status, int page, int size);

    /**
     * 删除账户（软删除）
     */
    Mono<Void> deleteById(Long id);

    /**
     * 自动生成账户编码
     */
    Mono<String> generateAccountCode(Long userId);

    /**
     * 更新账户余额
     */
    Mono<FinAccountDto> updateBalance(Long accountId, java.math.BigDecimal newBalance);
}
