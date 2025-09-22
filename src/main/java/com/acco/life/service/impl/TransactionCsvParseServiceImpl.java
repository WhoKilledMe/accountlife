package com.acco.life.service.impl;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionLabelDetail;
import com.acco.life.dto.FileTransactionNingBoBank;
import com.acco.life.entity.AccountTransaction;
import com.acco.life.entity.UserAccount;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.factory.FileTransactionFactory;
import com.acco.life.mapper.AccountTransactionMapper;
import com.acco.life.repository.AccountTransactionRepository;
import com.acco.life.repository.UserAccountRepository;
import com.acco.life.service.AiTransactionCategoryService;
import com.acco.life.service.TransactionCsvParseService;
import com.acco.life.service.csv.CsvParserStrategy;
import com.acco.life.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * description: 交易CSV解析服务实现类，实现CSV文件解析与入库逻辑
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionCsvParseServiceImpl implements TransactionCsvParseService {
    private final AccountTransactionMapper mapper;
    private final AccountTransactionRepository transactionRepository;
    private final UserAccountRepository accountRepository;
    private final AiTransactionCategoryService aiTransactionCategoryService;
    private final java.util.List<CsvParserStrategy> parsers;

    @Override
    public Mono<Void> parseCsvFile(InputStream inputStream, TransactionSourceType transactionSourceType, String accountName) {
        Class<? extends FileTransactionDto> targetType = FileTransactionFactory.fileTransactionMap.get(transactionSourceType);
        if (targetType == null) {
            return Mono.error(new IllegalArgumentException("不支持的 type: " + transactionSourceType));
        }

        return UserUtil.getCurrentUserId()
                .flatMap(userId -> {
                    log.debug("开始解析CSV文件，用户ID: {}, 账户名称: {}", userId, accountName);
                    
                    // 根据用户ID和账户名称获取账户ID
                    return accountRepository.findByUserIdAndName(userId, accountName)
                            .map(UserAccount::getId)
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("未找到账户名称为: " + accountName + " 的账户")));
                })
                .flatMap(accountId -> {
                    log.debug("找到账户ID: {}", accountId);
                    
                    try {
                        CsvParserStrategy parser = parsers.stream()
                                .filter(p -> p.supports(transactionSourceType))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("未找到匹配的CSV解析器: " + transactionSourceType));
                        List<? extends FileTransactionDto> fileTransactionDtos;
                        try {
                            fileTransactionDtos = parser.parse(inputStream);
                        } catch (Exception ex) {
                            throw new IOException("CSV解析失败: " + ex.getMessage(), ex);
                        }
                        
                        if (fileTransactionDtos.isEmpty()) {
                            log.warn("CSV文件中没有找到任何交易记录");
                            return Mono.empty();
                        }
                        
                        log.info("CSV文件解析完成，共找到 {} 条交易记录", fileTransactionDtos.size());
                        
                        // 策略内完成 DTO->实体 的构建（包含AI分类）
                        List<Mono<AccountTransaction>> transactionMonos =
                                parser.buildTransactions(fileTransactionDtos, UserUtil.getCurrentUserId().block(), accountId, transactionSourceType, mapper, aiTransactionCategoryService);
                        
                        // 并行处理所有交易记录
                        return Flux.fromIterable(transactionMonos)
                                .flatMap(mono -> mono, 10) // 限制并发数为10
                                .collectList()
                                .flatMap(transactions -> {
                                    log.info("开始保存 {} 条交易记录到数据库", transactions.size());
                                    return transactionRepository.saveAll(transactions).then();
                                });
                        
                    } catch (IOException e) {
                        log.error("解析CSV文件时发生IO错误", e);
                        return Mono.error(new RuntimeException("解析CSV文件失败: " + e.getMessage(), e));
                    }
                });
    }
    
    /**
     * 处理单个交易DTO，转换为AccountTransaction实体
     */
    private Mono<AccountTransaction> processTransactionDto(FileTransactionDto dto, Long  accountId, TransactionSourceType transactionSourceType) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> {
                    if (dto instanceof FileTransactionNingBoBank ningBoBank) {
                        return processNingBoBankTransaction(ningBoBank, userId, accountId, transactionSourceType);
                    } else if (dto instanceof FileTransactionLabelDetail labelDetail) {
                        return processLabelDetailTransaction(labelDetail, userId, accountId, transactionSourceType);
                    } else {
                        log.warn("未知的交易DTO类型: {}", dto.getClass().getSimpleName());
                        return Mono.error(new IllegalArgumentException("不支持的交易DTO类型: " + dto.getClass().getSimpleName()));
                    }
                });
    }
    
    /**
     * 处理宁波银行交易记录
     */
    private Mono<AccountTransaction> processNingBoBankTransaction(FileTransactionNingBoBank ningbo, Long userId, Long  accountId, TransactionSourceType transactionSourceType) {
        return aiTransactionCategoryService
                .inferTransactionCategory(ningbo.getTransactionSummary(), ningbo.getTransactionAmount(), userId)
                .flatMap(category -> {
                    AccountTransaction transaction = mapper.toEntity(ningbo, userId, accountId, transactionSourceType.code);
                    if (category != null) {
                        transaction.setCategoryId(category.getId());
                        log.debug("AI分类成功，分类ID: {}", category.getId());
                    } else {
                        log.debug("AI分类失败，使用默认分类");
                    }
                    return Mono.just(transaction);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    AccountTransaction transaction = mapper.toEntity(ningbo, userId, accountId, transactionSourceType.code);
                    log.debug("AI分类失败，使用默认分类");
                    return Mono.just(transaction);
                }));
    }
    
    /**
     * 处理标签详情交易记录
     */
    private Mono<AccountTransaction> processLabelDetailTransaction(FileTransactionLabelDetail labelDetail, Long  userId, Long  accountId, TransactionSourceType transactionSourceType) {
        return aiTransactionCategoryService
                .inferTransactionCategory(labelDetail.getTransactionSummary(), labelDetail.getTransactionAmount(), userId)
                .flatMap(category -> {
                    AccountTransaction transaction = mapper.toEntity(labelDetail, userId, accountId, transactionSourceType.code);
                    if (category != null) {
                        transaction.setCategoryId(category.getId());
                        log.debug("AI分类成功，分类ID: {}", category.getId());
                    } else {
                        log.debug("AI分类失败，使用默认分类");
                    }
                    return Mono.just(transaction);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    AccountTransaction transaction = mapper.toEntity(labelDetail, userId, accountId, transactionSourceType.code);
                    log.debug("AI分类失败，使用默认分类");
                    return Mono.just(transaction);
                }));
    }

    
}