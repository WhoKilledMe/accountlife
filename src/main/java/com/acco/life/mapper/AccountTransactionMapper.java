package com.acco.life.mapper;

import com.acco.life.dto.AccountTransactionDto;
import com.acco.life.dto.FileTransactionDto;
import com.acco.life.dto.FileTransactionLabelDetail;
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

    /**
     * 将AccountTransaction转换为包含账户名称的DTO
     * 需要额外查询asset_account表获取账户名称
     */
    default AccountTransactionDto toDtoWithAccountName(AccountTransaction entity, String accountName) {
        AccountTransactionDto dto = toDto(entity);
        dto.setAccountName(accountName);
        return dto;
    }

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
    AccountTransaction toEntity(FileTransactionDto fileTransactionDto, Long userId, Long accountId, Integer sourceType);

    default List<AccountTransaction> toEntityList(List<? extends FileTransactionDto> dtoList, 
                                                  TransactionSourceType transactionSourceType,
                                                  Long userId,
                                                  Long accountId) {
        List<AccountTransaction> accountTransactions = new ArrayList<>();
        for (FileTransactionDto dto : dtoList) {
            AccountTransaction transaction = toEntity(dto, userId, accountId, transactionSourceType.code);
            accountTransactions.add(transaction);
        }
        return accountTransactions;
    }

    @Named("determineTransactionType")
    default Integer determineTransactionType(FileTransactionDto fileTransactionDto) {
        // 根据金额判断交易类型，正数为收入，负数为支出
        if (fileTransactionDto instanceof FileTransactionNingBoBank || fileTransactionDto instanceof FileTransactionLabelDetail) {
            String amount = null;
            String summary = null;
            
            if (fileTransactionDto instanceof FileTransactionNingBoBank) {
                FileTransactionNingBoBank ningbo = (FileTransactionNingBoBank) fileTransactionDto;
                amount = ningbo.getTransactionAmount();
                summary = ningbo.getTransactionSummary();
            } else if (fileTransactionDto instanceof FileTransactionLabelDetail) {
                FileTransactionLabelDetail labelDetail = (FileTransactionLabelDetail) fileTransactionDto;
                amount = labelDetail.getTransactionAmount();
                summary = labelDetail.getTransactionSummary();
            }
            
            if (amount != null) {
                try {
                    BigDecimal amountValue = new BigDecimal(amount);
                    if (amountValue.compareTo(BigDecimal.ZERO) > 0) {
                        return TransactionType.INCOME.code;
                    } else if (amountValue.compareTo(BigDecimal.ZERO) < 0) {
                        return TransactionType.EXPENSE.code;
                    }
                } catch (NumberFormatException e) {
                    // 解析失败，根据摘要判断
                }
            }
            
            // 如果金额无法判断，则根据交易摘要判断
            if (summary != null) {
                if (summary.contains("收入") || summary.contains("入账") || summary.contains("到账") || 
                    summary.contains("收款") || summary.contains("返现") || summary.contains("退款") ||
                    summary.contains("报销") || summary.contains("赔偿") || summary.contains("捐赠") ||
                    summary.contains("红包") || summary.contains("礼金") || summary.contains("中奖") ||
                    summary.contains("兼职") || summary.contains("副业") || summary.contains("咨询") ||
                    summary.contains("服务") || summary.contains("工资") || summary.contains("薪水") ||
                    summary.contains("奖金") || summary.contains("绩效") || summary.contains("提成") ||
                    summary.contains("补贴") || summary.contains("津贴") || summary.contains("利息") ||
                    summary.contains("分红")) {
                    return TransactionType.INCOME.code;
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
            if (transactionDate != null && !transactionDate.trim().isEmpty()) {
                try {
                    // 尝试解析日期格式：2025-07-02
                    if (transactionDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        return LocalDateTime.parse(transactionDate + "T00:00:00");
                    }
                    // 尝试解析日期格式：2025/07/02
                    else if (transactionDate.matches("\\d{4}/\\d{2}/\\d{2}")) {
                        String normalizedDate = transactionDate.replace("/", "-");
                        return LocalDateTime.parse(normalizedDate + "T00:00:00");
                    }
                    // 尝试解析日期格式：20250702
                    else if (transactionDate.matches("\\d{8}")) {
                        String year = transactionDate.substring(0, 4);
                        String month = transactionDate.substring(4, 6);
                        String day = transactionDate.substring(6, 8);
                        return LocalDateTime.parse(year + "-" + month + "-" + day + "T00:00:00");
                    }
                } catch (Exception e) {
                    // 解析失败，记录日志
                }
            }
        }
        // 如果无法解析，返回当前时间
        return LocalDateTime.now();
    }
}