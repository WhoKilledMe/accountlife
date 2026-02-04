package com.acco.life.service.impl;

import com.acco.life.dto.TransactionCategoryDto;
import com.acco.life.entity.TransactionCategory;
import com.acco.life.enums.CategoryKeywordMapping;
import com.acco.life.enums.CategoryType;
import com.acco.life.enums.TransactionType;
import com.acco.life.mapper.TransactionCategoryMapper;
import com.acco.life.repository.TransactionCategoryRepository;
import com.acco.life.service.AiTransactionCategoryService;
import com.acco.life.service.CategoryKeywordMappingService;
import com.acco.life.service.TransactionCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * description: AI交易分类服务实现类
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Service
@RequiredArgsConstructor
public class AiTransactionCategoryServiceImpl implements AiTransactionCategoryService {

    private final TransactionCategoryService transactionCategoryService;
    private final TransactionCategoryRepository transactionCategoryRepository;
    private final TransactionCategoryMapper transactionCategoryMapper;
    private final CategoryKeywordMappingService categoryKeywordMappingService;

    @Override
    public Mono<TransactionCategoryDto> inferTransactionCategory(String counterparty, String description, String amount, Long userId) {
        // 组合 counterparty 和 description 进行匹配（counterparty优先，因为平台名更准确）
        String combinedText = buildSearchText(counterparty, description);

        // 1. 优先使用数据库中的关键词映射表，直接命中 transaction_category 表的 ID
        return categoryKeywordMappingService.findCategoryIdByKeyword(combinedText, userId)
                .flatMap(categoryId -> transactionCategoryService.findById(categoryId))
                // 2. 若未命中任何关键词映射，则回退到枚举体系，按旧逻辑推断并按 name+type 在 transaction_category 中查找/创建
                .switchIfEmpty(Mono.defer(() -> {
                    CategoryType categoryType = inferCategoryType(combinedText, amount);
                    return findOrCreateCategory(categoryType, userId);
                }));
    }
    
    /**
     * 构建搜索文本：counterparty + description
     * counterparty（交易对方）优先级更高，因为平台名称比交易描述更准确
     */
    private String buildSearchText(String counterparty, String description) {
        StringBuilder sb = new StringBuilder();
        if (counterparty != null && !counterparty.isBlank()) {
            sb.append(counterparty).append(" ");
        }
        if (description != null && !description.isBlank()) {
            sb.append(description);
        }
        return sb.toString().trim();
    }

    @Override
    public TransactionType inferTransactionType(String transactionSummary, String amount) {
        try {
            BigDecimal amountDecimal = new BigDecimal(amount);
            // 正数表示收入，负数表示支出
            if (amountDecimal.compareTo(BigDecimal.ZERO) > 0) {
                return TransactionType.INCOME;
            } else if (amountDecimal.compareTo(BigDecimal.ZERO) < 0) {
                return TransactionType.EXPENSE;
            } else {
                // 金额为0，根据关键词判断
                if (transactionSummary.contains("转账") || transactionSummary.contains("转出") || transactionSummary.contains("转入")) {
                    return transactionSummary.contains("转入") ? TransactionType.TRANSFER_IN : TransactionType.TRANSFER_OUT;
                }
                return TransactionType.EXPENSE; // 默认支出
            }
        } catch (NumberFormatException e) {
            // 金额格式错误，根据关键词判断
            if (transactionSummary.contains("转账") || transactionSummary.contains("转出") || transactionSummary.contains("转入")) {
                return transactionSummary.contains("转入") ? TransactionType.TRANSFER_IN : TransactionType.TRANSFER_OUT;
            }
            return TransactionType.EXPENSE; // 默认支出
        }
    }

    @Override
    public CategoryType inferCategoryType(String transactionSummary, String amount) {
        // 使用关键词映射枚举查找分类
        CategoryType categoryType = CategoryKeywordMapping.findCategoryByKeyword(transactionSummary);
        
        if (categoryType != null) {
            return categoryType;
        }
        
        // 如果没有找到匹配的分类，根据交易类型返回默认分类
        TransactionType transactionType = inferTransactionType(transactionSummary, amount);
        return switch (transactionType) {
            case INCOME -> CategoryType.OTHER_INCOME;
            case TRANSFER_OUT -> CategoryType.ACCOUNT_TRANSFER;
            case TRANSFER_IN -> CategoryType.TRANSFER_IN;
            default -> CategoryType.OTHER_EXPENSE;
        };
    }

    @Override
    public List<TransactionType> getAllTransactionTypes() {
        return List.of(TransactionType.values());
    }

    @Override
    public List<CategoryType> getAllCategoryTypes() {
        return List.of(CategoryType.values());
    }

    @Override
    public List<CategoryType> getCategoryTypesByTransactionType(TransactionType transactionType) {
        return CategoryType.getByTransactionType(transactionType);
    }

    @Override
    public Mono<List<TransactionCategoryDto>> recommendTransactionCategories(String transactionSummary, String amount, Long userId, int topN) {
        // 基于当前推断的分类进行推荐
        CategoryType inferredCategory = inferCategoryType(transactionSummary, amount);
        TransactionType transactionType = inferTransactionType(transactionSummary, amount);
        
        // 获取同类型的其他分类作为推荐
        List<CategoryType> recommendations = getCategoryTypesByTransactionType(transactionType)
                .stream()
                .filter(category -> category != inferredCategory)
                .limit(topN - 1)
                .collect(Collectors.toList());
        
        // 将推断的分类放在第一位
        recommendations.add(0, inferredCategory);
        
        // 转换为DTO列表
        List<TransactionCategoryDto> result = recommendations.stream()
                .map(category -> {
                    TransactionCategoryDto dto = new TransactionCategoryDto();
                    dto.setType(category.transactionType.code);
                    dto.setName(category.name);
                    return dto;
                })
                .collect(Collectors.toList());
        
        return Mono.just(result);
    }

    @Override
    public Mono<List<TransactionCategoryDto>> batchInferTransactionCategories(List<TransactionSummary> transactions, Long userId) {
        List<TransactionCategoryDto> results = new ArrayList<>();
        
        for (TransactionSummary transaction : transactions) {
            TransactionType transactionType = inferTransactionType(transaction.getSummary(), transaction.getAmount());
            CategoryType categoryType = inferCategoryType(transaction.getSummary(), transaction.getAmount());
            
            TransactionCategoryDto dto = new TransactionCategoryDto();
            dto.setType(categoryType.transactionType.code);
            dto.setName(categoryType.name);
            
            results.add(dto);
        }
        
        return Mono.just(results);
    }

    /**
     * 查找或创建分类
     */
    private Mono<TransactionCategoryDto> findOrCreateCategory(CategoryType categoryType, Long userId) {
        return transactionCategoryRepository.findByTypeAndUserId(categoryType.transactionType.code,categoryType.name, userId)
                .map(transactionCategoryMapper::toDto)
                .switchIfEmpty(createNewCategory(categoryType, userId));
    }

    /**
     * 创建新分类
     */
    private Mono<TransactionCategoryDto> createNewCategory(CategoryType categoryType, Long userId) {
        TransactionCategory newCategory = new TransactionCategory();
        newCategory.setType(categoryType.transactionType.code);
        newCategory.setName(categoryType.name);
        if(userId != null) {
            newCategory.setUserId(userId);
        }

        return transactionCategoryRepository.save(newCategory)
                .map(transactionCategoryMapper::toDto);
    }
    
}