package com.acco.life.service;

import com.acco.life.dto.TransactionCategoryDto;
import com.acco.life.enums.CategoryType;
import com.acco.life.enums.TransactionType;
import reactor.core.publisher.Mono;
import java.util.List;

/**
 * description: AI交易分类服务接口
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface AiTransactionCategoryService {
    
    /**
     * 推断交易分类
     */
    Mono<TransactionCategoryDto> inferTransactionCategory(String transactionSummary, String amount, Integer userId);
    
    /**
     * 推断交易类型（基于金额）
     */
    TransactionType inferTransactionType(String transactionSummary, String amount);
    
    /**
     * 推断分类类型（基于关键词）
     */
    CategoryType inferCategoryType(String transactionSummary, String amount);
    
    /**
     * 获取所有交易类型
     */
    List<TransactionType> getAllTransactionTypes();
    
    /**
     * 获取所有分类类型
     */
    List<CategoryType> getAllCategoryTypes();
    
    /**
     * 根据交易类型获取对应的分类类型
     */
    List<CategoryType> getCategoryTypesByTransactionType(TransactionType transactionType);
    
    /**
     * 推荐交易分类
     */
    Mono<List<TransactionCategoryDto>> recommendTransactionCategories(String transactionSummary, String amount, Integer userId, int topN);
    
    /**
     * 批量推断交易分类
     */
    Mono<List<TransactionCategoryDto>> batchInferTransactionCategories(List<TransactionSummary> transactions, Integer userId);
    
    /**
     * 交易摘要内部类
     */
    class TransactionSummary {
        private String summary;
        private String amount;
        
        public TransactionSummary(String summary, String amount) {
            this.summary = summary;
            this.amount = amount;
        }
        
        public String getSummary() {
            return summary;
        }
        
        public String getAmount() {
            return amount;
        }
    }
} 