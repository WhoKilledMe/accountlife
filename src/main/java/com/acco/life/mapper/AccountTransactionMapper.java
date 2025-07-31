package com.acco.life.mapper;

import com.acco.life.dto.AccountTransactionDto;
import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionNingBoBank;
import com.acco.life.entity.AccountTransaction;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.enums.TransactionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * description: 账户交易映射器，负责DTO与实体的转换
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Mapper(componentModel = "spring")
public interface AccountTransactionMapper {

    AccountTransaction toEntity(AccountTransactionDto dto);

    AccountTransactionDto toDto(AccountTransaction entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "type", source = "fileTransactionDto", qualifiedByName = "determineTransactionType")
    @Mapping(target = "amount", source = "fileTransactionDto", qualifiedByName = "extractAmount")
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "relatedTransactionId", ignore = true)
    @Mapping(target = "description", source = "fileTransactionDto", qualifiedByName = "extractDescription")
    @Mapping(target = "transactionTime", source = "fileTransactionDto", qualifiedByName = "extractTransactionTime")
    @Mapping(target = "sourceType", source = "sourceType")
    @Mapping(target = "sourceRef", ignore = true)
    @Mapping(target = "statementId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    AccountTransaction toEntity(FileTransactionDto fileTransactionDto, Integer userId, Integer accountId, Integer sourceType);

    default List<AccountTransaction> toEntityList(List<? extends FileTransactionDto> dtoList, 
                                                  TransactionSourceType transactionSourceType,
                                                  Integer userId,
                                                  Integer accountId) {
        List<AccountTransaction> accountTransactions = new ArrayList<>();
        for (FileTransactionDto dto : dtoList) {
            AccountTransaction transaction = toEntity(dto, userId, accountId, transactionSourceType.code);
            accountTransactions.add(transaction);
        }
        return accountTransactions;
    }

    @Named("determineTransactionType")
    default Integer determineTransactionType(FileTransactionDto fileTransactionDto) {
        // 简单实现，后续根据业务规则完善
        if (fileTransactionDto instanceof FileTransactionNingBoBank) {
            // 根据交易摘要判断交易类型，示例逻辑
            FileTransactionNingBoBank ningbo = (FileTransactionNingBoBank) fileTransactionDto;
            String summary = ningbo.getTransactionSummary();
            if (summary != null) {
                if (summary.contains("收入") || summary.contains("入账")) {
                    return TransactionType.INCOME.code;
                } else {
                    return TransactionType.EXPENSE.code;
                }
            }
        }
        // 默认为支出
        return TransactionType.EXPENSE.code;
    }

    @Named("extractAmount")
    default BigDecimal extractAmount(FileTransactionDto fileTransactionDto) {
        if (fileTransactionDto instanceof FileTransactionNingBoBank) {
            FileTransactionNingBoBank ningbo = (FileTransactionNingBoBank) fileTransactionDto;
            if (ningbo.getTransactionAmount() != null) {
                try {
                    return new BigDecimal(ningbo.getTransactionAmount());
                } catch (NumberFormatException e) {
                    // 解析失败，返回0
                    return BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    @Named("extractDescription")
    default String extractDescription(FileTransactionDto fileTransactionDto) {
        if (fileTransactionDto instanceof FileTransactionNingBoBank) {
            FileTransactionNingBoBank ningbo = (FileTransactionNingBoBank) fileTransactionDto;
            return ningbo.getTransactionSummary();
        }
        return "";
    }

    @Named("extractTransactionTime")
    default LocalDateTime extractTransactionTime(FileTransactionDto fileTransactionDto) {
        if (fileTransactionDto instanceof FileTransactionNingBoBank) {
            FileTransactionNingBoBank ningbo = (FileTransactionNingBoBank) fileTransactionDto;
            String transactionDate = ningbo.getTransactionDate();
            if (transactionDate != null) {
                // 简单示例，实际可能需要解析具体日期格式
                try {
                    return LocalDateTime.now(); // 实际应该解析transactionDate字符串
                } catch (Exception e) {
                    return LocalDateTime.now();
                }
            }
        }
        return LocalDateTime.now();
    }
}