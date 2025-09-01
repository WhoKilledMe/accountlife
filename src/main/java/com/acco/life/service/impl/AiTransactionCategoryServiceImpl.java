package com.acco.life.service.impl;

import com.acco.life.dto.TransactionCategoryDto;
import com.acco.life.entity.TransactionCategory;
import com.acco.life.enums.CategoryKeywordMapping;
import com.acco.life.enums.CategoryType;
import com.acco.life.enums.TransactionType;
import com.acco.life.mapper.TransactionCategoryMapper;
import com.acco.life.repository.TransactionCategoryRepository;
import com.acco.life.service.AiTransactionCategoryService;
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

    private final TransactionCategoryRepository transactionCategoryRepository;
    private final TransactionCategoryMapper transactionCategoryMapper;

    @Override
    public Mono<TransactionCategoryDto> inferTransactionCategory(String transactionSummary, String amount, Integer userId) {
        // 推断交易类型和分类类型
        TransactionType transactionType = inferTransactionType(transactionSummary, amount);
        CategoryType categoryType = inferCategoryType(transactionSummary, amount);
        
        // 查找或创建分类
        return findOrCreateCategory(categoryType, userId);
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
        switch (transactionType) {
            case INCOME:
                return CategoryType.OTHER_INCOME;
            case TRANSFER_OUT:
                return CategoryType.ACCOUNT_TRANSFER;
            case TRANSFER_IN:
                return CategoryType.TRANSFER_IN;
            case EXPENSE:
            default:
                return CategoryType.OTHER_EXPENSE;
        }
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
    public Mono<List<TransactionCategoryDto>> recommendTransactionCategories(String transactionSummary, String amount, Integer userId, int topN) {
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
    public Mono<List<TransactionCategoryDto>> batchInferTransactionCategories(List<TransactionSummary> transactions, Integer userId) {
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
    private Mono<TransactionCategoryDto> findOrCreateCategory(CategoryType categoryType, Integer userId) {
        return transactionCategoryRepository.findByTypeAndUserId(categoryType.transactionType.code,categoryType.name, userId)
                .map(transactionCategoryMapper::toDto)
                .switchIfEmpty(createNewCategory(categoryType, userId));
    }

    /**
     * 创建新分类
     */
    private Mono<TransactionCategoryDto> createNewCategory(CategoryType categoryType, Integer userId) {
        TransactionCategory newCategory = new TransactionCategory();
        newCategory.setType(categoryType.transactionType.code);
        newCategory.setName(categoryType.name);
        if(userId != null) {
            newCategory.setUserId(userId);
        }

        return transactionCategoryRepository.save(newCategory)
                .map(transactionCategoryMapper::toDto);
    }
    
    /**
     * 测试新的交易类型系统
     */
    public void testNewTransactionTypes() {
        System.out.println("=== 测试新的交易类型系统 ===");
        
        // 测试交易类型推断
        String[] testSummaries = {
            "工资发放", "餐饮消费", "转账到支付宝", "投资收益", "购物消费", "医疗费用"
        };
        
        String[] testAmounts = {
            "5000.00", "-100.00", "-1000.00", "200.00", "-500.00", "-200.00"
        };
        
        for (int i = 0; i < testSummaries.length; i++) {
            TransactionType transactionType = inferTransactionType(testSummaries[i], testAmounts[i]);
            CategoryType categoryType = inferCategoryType(testSummaries[i], testAmounts[i]);
            
            System.out.printf("摘要: %s, 金额: %s\n", testSummaries[i], testAmounts[i]);
            System.out.printf("推断交易类型: %s (%s)\n", transactionType.name, transactionType.name);
            System.out.printf("推断分类类型: %s (%s)\n", categoryType.name, categoryType.name);
            System.out.println("---");
        }
        
        // 测试关键词映射
        System.out.println("=== 测试关键词映射 ===");
        String[] testKeywords = {"工资", "餐饮", "转账", "投资", "购物", "医疗"};
        for (String keyword : testKeywords) {
            CategoryType category = CategoryKeywordMapping.findCategoryByKeyword(keyword);
            if (category != null) {
                System.out.printf("关键词 '%s' -> 分类: %s\n", keyword, category.name);
            } else {
                System.out.printf("关键词 '%s' -> 未找到匹配分类\n", keyword);
            }
        }
    }
    
    /**
     * 主方法，用于测试整个系统
     */
    public static void main(String[] args) {
        // 创建测试实例
        AiTransactionCategoryServiceImpl testService = new AiTransactionCategoryServiceImpl(null, null);
        
        // 运行测试
        testService.testNewTransactionTypes();
        
        System.out.println("\n=== 测试完成 ===");
    }
} 