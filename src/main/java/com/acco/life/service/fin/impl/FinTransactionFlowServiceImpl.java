package com.acco.life.service.fin.impl;

import com.acco.life.dto.fin.FinTransactionDto;
import com.acco.life.entity.fin.*;
import com.acco.life.enums.fin.BizType;
import com.acco.life.enums.fin.TransactionStatus;
import com.acco.life.mapper.fin.FinPaymentRouteMapper;
import com.acco.life.mapper.fin.FinTransactionMapper;
import com.acco.life.repository.fin.*;
import com.acco.life.repository.fin.FinAccountFlowRepository;
import com.acco.life.repository.fin.FinClearingFlowRepository;
import com.acco.life.service.fin.FinAccountFlowService;
import com.acco.life.service.fin.FinClearingFlowService;
import com.acco.life.service.fin.FinReconciliationService;
import com.acco.life.service.fin.FinTransactionFlowService;
import com.acco.life.service.fin.FinTransactionService;
import com.acco.life.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 完整交易流程服务实现
 * 支持一键同步：交易同步、支付链路梳理、清算同步、交易流程同步
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinTransactionFlowServiceImpl implements FinTransactionFlowService {

    private final FinTransactionService transactionService;
    private final FinReconciliationService reconciliationService;
    private final FinClearingFlowService clearingFlowService;
    private final FinAccountFlowService accountFlowService;
    private final FinTransactionRepository transactionRepository;
    private final FinStatementRepository statementRepository;
    private final FinStatementAccountMapRepository statementAccountMapRepository;
    private final FinTransactionStatementMapRepository transactionStatementMapRepository;
    private final FinPaymentRouteRepository paymentRouteRepository;
    private final FinPaymentMethodRepository paymentMethodRepository;
    private final FinAccountRepository accountRepository;
    private final FinClearingFlowRepository clearingFlowRepository;
    private final FinAccountFlowRepository accountFlowRepository;
    private final FinTransactionMapper transactionMapper;
    private final FinPaymentRouteMapper paymentRouteMapper;

    @Override
    @Transactional
    public Mono<FinTransactionDto> createCompleteTransactionFlow(List<Long> statementIds, FinTransactionDto transactionDto) {
        return Flux.fromIterable(statementIds)
                .flatMap(statementRepository::findById)
                .collectList()
                .flatMap(statements -> {
                    if (statements.isEmpty()) {
                        return Mono.error(new IllegalArgumentException("未找到账单记录"));
                    }

                    // 1. 创建或获取主交易
                    Mono<FinTransaction> transactionMono = createOrGetTransaction(statements, transactionDto);

                    return transactionMono
                            .flatMap(transaction -> {
                                // 2. 自动映射账单到账户
                                return autoMapStatementsToAccounts(statements)
                                        .then(Mono.just(transaction));
                            })
                            .flatMap(transaction -> {
                                // 3. 自动映射账单到交易
                                return autoMapStatementsToTransaction(statements, transaction.getId())
                                        .then(Mono.just(transaction));
                            })
                            .flatMap(transaction -> {
                                // 4. 创建支付路由（根据账单信息推断）
                                return createPaymentRoutesFromStatements(transaction.getId(), statements)
                                        .then(Mono.just(transaction));
                            })
                            .flatMap(transaction -> {
                                // 5. 创建清算流水（如果涉及银行清算）
                                return createClearingFlowsFromStatements(transaction.getId(), statements)
                                        .then(Mono.just(transaction));
                            })
                            .flatMap(transaction -> {
                                // 6. 创建会计流水（复式记账）
                                return createAccountFlowsFromTransaction(transaction.getId(), statements)
                                        .then(Mono.just(transaction));
                            })
                            .flatMap(transaction -> {
                                // 7. 处理补贴（如果账单中有优惠信息）
                                return processSubsidyFromStatements(transaction, statements)
                                        .then(Mono.just(transaction));
                            })
                            .flatMap(transaction -> {
                                // 8. 更新交易状态为 CONFIRMED
                                transaction.setStatus(TransactionStatus.CONFIRMED.getCode());
                                return transactionRepository.save(transaction);
                            })
                            .map(transactionMapper::toDto);
                });
    }

    @Override
    @Transactional
    public Mono<FinTransactionDto> confirmAndSettleTransaction(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .flatMap(transaction -> {
                    // 1. 更新交易状态
                    transaction.setStatus(TransactionStatus.CONFIRMED.getCode());
                    return transactionRepository.save(transaction);
                })
                .flatMap(transaction -> {
                    // 2. 获取关联的账单
                    return transactionStatementMapRepository.findByTransactionId(transactionId)
                            .flatMap(map -> statementRepository.findById(map.getStatementId()))
                            .collectList()
                            .flatMap(statements -> {
                                // 3. 创建清算流水（如果还未创建）
                                return createClearingFlowsFromStatements(transactionId, statements)
                                        .then(Mono.just(transaction));
                            });
                })
                .flatMap(transaction -> {
                    // 4. 创建会计流水（如果还未创建）
                    return transactionStatementMapRepository.findByTransactionId(transactionId)
                            .flatMap(map -> statementRepository.findById(map.getStatementId()))
                            .collectList()
                            .flatMap(statements -> {
                                return createAccountFlowsFromTransaction(transactionId, statements)
                                        .then(Mono.just(transaction));
                            });
                })
                .map(transactionMapper::toDto);
    }

    @Override
    public Mono<List<PaymentRouteInfo>> createPaymentRoutes(Long transactionId, List<PaymentRouteInfo> routes) {
        return Flux.fromIterable(routes)
                .index()
                .flatMap(tuple -> {
                    PaymentRouteInfo route = tuple.getT2();
                    long index = tuple.getT1();

                    FinPaymentRoute entity = new FinPaymentRoute();
                    entity.setTransactionId(transactionId);
                    entity.setPaymentMethodId(route.paymentMethodId());
                    entity.setFromAccountId(route.fromAccountId());
                    entity.setToAccountId(route.toAccountId());
                    entity.setAmount(route.amount());
                    entity.setRouteOrder(route.routeOrder() != null ? route.routeOrder() : (int) (index + 1));
                    entity.setRouteType(route.routeType());

                    return paymentRouteRepository.save(entity);
                })
                .collectList()
                .map(saved -> routes);
    }

    @Override
    public Mono<Long> createClearingFlow(Long transactionId, Long fromAccountId, Long toAccountId,
                                         BigDecimal amount, String remark) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> {
                    String flowNo = generateClearingFlowNo(transactionId);
                    LocalDateTime tradeTime = LocalDateTime.now();

                    return clearingFlowService.createClearingFlow(
                            userId, fromAccountId, toAccountId, transactionId,
                            amount, "CNY", tradeTime, remark
                    ).map(flow -> flow.getId());
                });
    }

    @Override
    public Mono<List<Long>> createAccountFlow(Long transactionId, Long debitAccountId, Long creditAccountId,
                                               BigDecimal amount, String bizType, String remark) {
        return UserUtil.getCurrentUserId()
                .flatMap(userId -> accountFlowService.createDoubleEntry(
                        userId, debitAccountId, creditAccountId, amount, transactionId, bizType, remark
                ))
                .map(flows -> flows.stream().map(flow -> flow.getId()).toList());
    }

    @Override
    @Transactional
    public Mono<FinTransactionDto> createSubsidyTransaction(Long originalTransactionId, BigDecimal subsidyAmount,
                                                             Long fromAccountId, Long toAccountId, String remark) {
        return transactionRepository.findById(originalTransactionId)
                .flatMap(originalTx -> {
                    // 创建补贴交易
                    FinTransactionDto subsidyTx = new FinTransactionDto();
                    subsidyTx.setUserId(originalTx.getUserId());
                    subsidyTx.setBizType("SUBSIDY");
                    subsidyTx.setBizSubType("PLATFORM_SUBSIDY");
                    subsidyTx.setAmount(subsidyAmount);
                    subsidyTx.setCurrency("CNY");
                    subsidyTx.setStatus(TransactionStatus.CONFIRMED.getCode());
                    subsidyTx.setCounterparty(originalTx.getCounterparty());
                    subsidyTx.setPlatformCode(originalTx.getPlatformCode());
                    subsidyTx.setOriginalTransactionId(originalTransactionId);
                    subsidyTx.setRemark(remark != null ? remark : "平台补贴");
                    subsidyTx.setTradeTime(originalTx.getTradeTime());

                    return transactionService.create(subsidyTx)
                            .flatMap(subsidy -> {
                                // 创建补贴的会计流水
                                return createAccountFlow(
                                        subsidy.getId(), toAccountId, fromAccountId,
                                        subsidyAmount, "SUBSIDY",
                                        "平台补贴（借）" + toAccountId + " +" + subsidyAmount
                                ).thenReturn(subsidy);
                            });
                });
    }

    // ========== 私有辅助方法 ==========

    /**
     * 创建或获取主交易
     */
    private Mono<FinTransaction> createOrGetTransaction(List<FinStatement> statements, FinTransactionDto transactionDto) {
        if (transactionDto != null && transactionDto.getId() != null) {
            // 如果提供了交易ID，直接返回
            return transactionRepository.findById(transactionDto.getId())
                    .switchIfEmpty(Mono.error(new IllegalArgumentException("交易不存在: " + transactionDto.getId())));
        }

        // 从账单推断交易信息
        FinStatement first = statements.get(0);
        BigDecimal totalAmount = statements.stream()
                .map(FinStatement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        FinTransactionDto txDto = transactionDto != null ? transactionDto : new FinTransactionDto();
        if (txDto.getUserId() == null) {
            txDto.setUserId(first.getUserId());
        }
        if (txDto.getBizType() == null) {
            txDto.setBizType(determineBizType(first));
        }
        if (txDto.getTradeTime() == null) {
            txDto.setTradeTime(first.getStmtTime());
        }
        if (txDto.getAmount() == null) {
            txDto.setAmount(totalAmount);
        }
        if (txDto.getCurrency() == null) {
            txDto.setCurrency("CNY");
        }
        if (txDto.getStatus() == null) {
            txDto.setStatus(TransactionStatus.MATCHED.getCode());
        }
        if (txDto.getCounterparty() == null) {
            txDto.setCounterparty(first.getCounterparty());
        }
        if (txDto.getPlatformCode() == null) {
            txDto.setPlatformCode(first.getPlatformCode());
        }
        if (txDto.getCategoryId() == null) {
            txDto.setCategoryId(first.getCategoryId());
        }
        if (txDto.getRemark() == null) {
            txDto.setRemark("合并自 " + statements.size() + " 条账单");
        }

        return transactionService.create(txDto)
                .map(transactionMapper::toEntity);
    }

    /**
     * 自动映射账单到账户
     */
    private Mono<Void> autoMapStatementsToAccounts(List<FinStatement> statements) {
        return Flux.fromIterable(statements)
                .flatMap(statement -> {
                    // 检查是否已映射
                    return statementAccountMapRepository.findByStatementId(statement.getId())
                            .hasElements()
                            .flatMap(hasMapping -> {
                                if (hasMapping) {
                                    return Mono.empty();
                                }
                                // 尝试自动映射
                                return reconciliationService.autoMapStatementToAccount(statement.getUserId())
                                        .then();
                            });
                })
                .then();
    }

    /**
     * 自动映射账单到交易
     */
    private Mono<Void> autoMapStatementsToTransaction(List<FinStatement> statements, Long transactionId) {
        return Flux.fromIterable(statements)
                .flatMap(statement -> {
                    // 检查是否已映射
                    return transactionStatementMapRepository.findByTransactionIdAndStatementId(transactionId, statement.getId())
                            .hasElement()
                            .flatMap(hasMapping -> {
                                if (hasMapping) {
                                    return Mono.empty();
                                }
                                // 创建映射
                                return reconciliationService.manualMergeStatementToTransaction(
                                        statement.getId(), transactionId, "ONE_TO_ONE"
                                ).then();
                            });
                })
                .then();
    }

    /**
     * 根据账单信息创建支付路由
     */
    private Mono<Void> createPaymentRoutesFromStatements(Long transactionId, List<FinStatement> statements) {
        // 检查是否已有支付路由
        return paymentRouteRepository.findByTransactionId(transactionId)
                .hasElements()
                .flatMap(hasRoutes -> {
                    if (hasRoutes) {
                        return Mono.empty();
                    }

                    // 根据账单推断支付路由（响应式）
                    return Flux.fromIterable(statements)
                            .index()
                            .flatMap(tuple -> {
                                FinStatement statement = tuple.getT2();
                                long index = tuple.getT1();

                                // 根据账单的 account_ref 查找账户
                                return findAccountByRef(statement.getAccountRef(), statement.getUserId())
                                        .flatMap(account -> {
                                            // 根据平台和支付方式查找支付方式
                                            return findPaymentMethodByPlatform(statement.getPlatformCode())
                                                    .flatMap(paymentMethod -> {
                                                        // 推断目标账户（通常是商户账户）
                                                        return findMerchantAccount(statement.getPlatformCode(), statement.getUserId())
                                                                .map(merchantAccount -> {
                                                                    PaymentRouteInfo route = new PaymentRouteInfo(
                                                                            paymentMethod.getId(),
                                                                            account.getId(),
                                                                            merchantAccount.getId(),
                                                                            statement.getAmount(),
                                                                            (int) (index + 1),
                                                                            "NORMAL"
                                                                    );
                                                                    return route;
                                                                });
                                                    });
                                        })
                                        .switchIfEmpty(Mono.empty());
                            })
                            .collectList()
                            .flatMap(routes -> {
                                if (routes.isEmpty()) {
                                    return Mono.empty();
                                }
                                return createPaymentRoutes(transactionId, routes).then();
                            });
                });
    }

    /**
     * 根据账单创建清算流水
     */
    private Mono<Void> createClearingFlowsFromStatements(Long transactionId, List<FinStatement> statements) {
        return Flux.fromIterable(statements)
                .filter(stmt -> "BANK_STATEMENT".equals(stmt.getSourceType()) || "NINGBO".equals(stmt.getPlatformCode()))
                .flatMap(statement -> {
                    // 查找银行账户
                    return findAccountByRef(statement.getAccountRef(), statement.getUserId())
                            .flatMap(bankAccount -> {
                                // 查找目标账户（通常是支付平台账户）
                                return statementAccountMapRepository.findByStatementId(statement.getId())
                                        .next()
                                        .flatMap(map -> accountRepository.findById(map.getAccountId()))
                                        .flatMap(targetAccount -> {
                                            return createClearingFlow(
                                                    transactionId,
                                                    bankAccount.getId(),
                                                    targetAccount.getId(),
                                                    statement.getAmount(),
                                                    "银行清算: " + statement.getDescription()
                                            ).then();
                                        });
                            });
                })
                .then();
    }

    /**
     * 根据交易创建会计流水（复式记账）
     */
    private Mono<Void> createAccountFlowsFromTransaction(Long transactionId, List<FinStatement> statements) {
        return transactionRepository.findById(transactionId)
                .flatMap(transaction -> {
                    // 查找交易的支付路由，推断借贷关系
                    return paymentRouteRepository.findByTransactionId(transactionId)
                            .collectList()
                            .flatMap(routes -> {
                                if (routes.isEmpty()) {
                                    return Mono.empty();
                                }

                                // 根据支付路由创建会计流水
                                // 借：目标账户（商户账户）
                                // 贷：来源账户（用户账户）
                                return Flux.fromIterable(routes)
                                        .flatMap(route -> {
                                            if (route.getFromAccountId() != null && route.getToAccountId() != null) {
                                                return createAccountFlow(
                                                        transactionId,
                                                        route.getToAccountId(),    // 借：商户账户
                                                        route.getFromAccountId(),  // 贷：用户账户
                                                        route.getAmount(),
                                                        transaction.getBizType(),
                                                        "交易清算: " + transaction.getRemark()
                                                ).then();
                                            }
                                            return Mono.empty();
                                        })
                                        .then();
                            });
                });
    }

    /**
     * 处理补贴（从账单中提取优惠信息）
     */
    private Mono<Void> processSubsidyFromStatements(FinTransaction transaction, List<FinStatement> statements) {
        // 查找微信账单中的优惠信息
        return Flux.fromIterable(statements)
                .filter(stmt -> "WECHAT".equals(stmt.getPlatformCode()))
                .next()
                .flatMap(wechatStatement -> {
                    // 从 raw_data 中提取优惠金额
                    BigDecimal subsidyAmount = extractSubsidyAmount(wechatStatement);
                    if (subsidyAmount != null && subsidyAmount.compareTo(BigDecimal.ZERO) > 0) {
                        // 查找补贴账户和商户账户
                        return findSubsidyAccount(transaction.getUserId())
                                .zipWith(findMerchantAccount(transaction.getPlatformCode(), transaction.getUserId()))
                                .flatMap(tuple -> {
                                    Long subsidyAccountId = tuple.getT1().getId();
                                    Long merchantAccountId = tuple.getT2().getId();

                                    return createSubsidyTransaction(
                                            transaction.getId(),
                                            subsidyAmount,
                                            subsidyAccountId,
                                            merchantAccountId,
                                            "微信备注: 已优惠¥" + subsidyAmount + "（平台补贴）"
                                    ).then();
                                });
                    }
                    return Mono.empty();
                })
                .then();
    }

    /**
     * 从账单的 raw_data 中提取优惠金额
     */
    private BigDecimal extractSubsidyAmount(FinStatement statement) {
        // TODO: 解析 JSON 提取优惠金额
        // 示例：从 "已优惠¥0.10" 中提取 0.10
        if (statement.getRawData() != null && statement.getRawData().contains("已优惠")) {
            // 简单实现，实际应该解析 JSON
            try {
                String rawData = statement.getRawData();
                int start = rawData.indexOf("已优惠¥") + 4;
                int end = rawData.indexOf("，", start);
                if (end == -1) end = rawData.length();
                String amountStr = rawData.substring(start, end).trim();
                return new BigDecimal(amountStr);
            } catch (Exception e) {
                log.warn("提取优惠金额失败", e);
            }
        }
        return null;
    }

    /**
     * 根据账户标识查找账户
     * 优先通过 account_code 匹配，其次通过 external_account_ref 匹配
     */
    private Mono<FinAccount> findAccountByRef(String accountRef, Long userId) {
        if (accountRef == null || accountRef.isEmpty()) {
            return Mono.empty();
        }

        // 先尝试通过 account_code 精确匹配
        return accountRepository.findByUserIdAndAccountCode(userId, accountRef)
                .switchIfEmpty(
                        // 如果 account_code 不匹配，尝试通过 external_account_ref 匹配
                        accountRepository.findByUserId(userId)
                                .filter(account -> accountRef.equals(account.getExternalAccountRef()))
                                .next()
                )
                .switchIfEmpty(
                        // 最后尝试通过账户名称模糊匹配
                        accountRepository.findByUserIdAndAccountNameLike(userId, "%" + accountRef + "%")
                                .next()
                );
    }

    /**
     * 根据平台查找支付方式
     */
    private Mono<FinPaymentMethod> findPaymentMethodByPlatform(String platformCode) {
        return paymentMethodRepository.findByPlatformCode(platformCode)
                .next();
    }

    /**
     * 查找商户账户
     */
    private Mono<FinAccount> findMerchantAccount(String platformCode, Long userId) {
        return accountRepository.findByUserIdAndPlatformCode(userId, platformCode)
                .filter(account -> "MERCHANT".equals(account.getOwnerType()))
                .next();
    }

    /**
     * 查找补贴账户
     */
    private Mono<FinAccount> findSubsidyAccount(Long userId) {
        return accountRepository.findByUserId(userId)
                .filter(account -> account.getIsVirtual() != null && account.getIsVirtual().equals(1))
                .filter(account -> account.getAccountName().contains("补贴"))
                .next();
    }

    /**
     * 确定业务类型
     */
    private String determineBizType(FinStatement statement) {
        if ("IN".equals(statement.getDirection())) {
            return BizType.INCOME.getCode();
        } else if ("OUT".equals(statement.getDirection())) {
            return BizType.PAY.getCode();
        }
        return BizType.PAY.getCode();
    }

    /**
     * 生成清算流水号
     */
    private String generateClearingFlowNo(Long transactionId) {
        return "CL-" + transactionId + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    @Override
    @Transactional
    public Mono<SyncResult> syncAll(Long userId) {
        Mono<Long> userIdMono = userId != null 
                ? Mono.just(userId)
                : UserUtil.getCurrentUserId();

        return userIdMono
                .flatMap(uid -> {
                    // 1. 查找所有待处理的账单（状态为 NEW 或 PARSED）
                    return statementRepository.findPendingReconciliation(uid)
                            .collectList()
                            .flatMap(statements -> {
                                if (statements.isEmpty()) {
                                    return Mono.just(new SyncResult(0, 0, 0, 0, 0));
                                }

                                // 2. 按订单号分组账单（同一笔交易的多条账单）
                                return Mono.fromCallable(() -> groupStatementsByOrder(statements))
                                        .flatMap(statementGroups -> {
                                            // 3. 对每组账单执行完整流程
                                            return Flux.fromIterable(statementGroups.entrySet())
                                                    .flatMap(entry -> {
                                                        List<Long> statementIds = entry.getValue().stream()
                                                                .map(FinStatement::getId)
                                                                .toList();

                                                        return createCompleteTransactionFlow(statementIds, null);
                                                    })
                                                    .collectList()
                                                    .flatMap(transactions -> {
                                                        if (transactions.isEmpty()) {
                                                            return Mono.just(new SyncResult(0, 0, 0, 0, 0));
                                                        }

                                                        // 统计结果
                                                        int processedTransactions = transactions.size();
                                                        
                                                        // 收集所有交易ID
                                                        List<Long> transactionIds = transactions.stream()
                                                                .map(FinTransactionDto::getId)
                                                                .toList();

                                                        // 统计支付路由
                                                        return Flux.fromIterable(transactionIds)
                                                                .flatMap(paymentRouteRepository::findByTransactionId)
                                                                .count()
                                                                .defaultIfEmpty(0L)
                                                                .flatMap(paymentRoutesCount -> {
                                                                    // 统计清算流水
                                                                    return Flux.fromIterable(transactionIds)
                                                                            .flatMap(clearingFlowRepository::findByTransactionId)
                                                                            .count()
                                                                            .defaultIfEmpty(0L)
                                                                            .flatMap(clearingFlowsCount -> {
                                                                                // 统计会计流水
                                                                                return Flux.fromIterable(transactionIds)
                                                                                        .flatMap(accountFlowRepository::findByTransactionId)
                                                                                        .count()
                                                                                        .defaultIfEmpty(0L)
                                                                                        .flatMap(accountFlowsCount -> {
                                                                                            // 统计补贴交易
                                                                                            return Flux.fromIterable(transactionIds)
                                                                                                    .flatMap(transactionRepository::findByOriginalTransactionId)
                                                                                                    .filter(tx -> "SUBSIDY".equals(tx.getBizType()))
                                                                                                    .count()
                                                                                                    .defaultIfEmpty(0L)
                                                                                                    .map(subsidiesCount -> new SyncResult(
                                                                                                            processedTransactions,
                                                                                                            (int) paymentRoutesCount.longValue(),
                                                                                                            (int) clearingFlowsCount.longValue(),
                                                                                                            (int) accountFlowsCount.longValue(),
                                                                                                            (int) subsidiesCount.longValue()
                                                                                                    ));
                                                                                        });
                                                                            });
                                                                });
                                                    });
                                        });
                            });
                });
    }

    /**
     * 按订单号分组账单
     */
    private java.util.Map<String, List<FinStatement>> groupStatementsByOrder(List<FinStatement> statements) {
        java.util.Map<String, List<FinStatement>> groups = new java.util.HashMap<>();
        
        for (FinStatement stmt : statements) {
            // 优先使用商户订单号，其次使用平台订单号，最后使用组合键
            String key;
            if (stmt.getRawData() != null && stmt.getRawData().contains("merchant_order_no")) {
                // 从 raw_data 中提取商户订单号
                try {
                    // 简单实现，实际应该解析 JSON
                    String rawData = stmt.getRawData();
                    int start = rawData.indexOf("\"merchant_order_no\":\"") + 21;
                    int end = rawData.indexOf("\"", start);
                    if (end > start) {
                        key = rawData.substring(start, end);
                    } else {
                        key = stmt.getOutTradeNo() != null ? stmt.getOutTradeNo() 
                                : stmt.getPlatformCode() + "_" + stmt.getStmtTime() + "_" + stmt.getAmount();
                    }
                } catch (Exception e) {
                    key = stmt.getOutTradeNo() != null ? stmt.getOutTradeNo() 
                            : stmt.getPlatformCode() + "_" + stmt.getStmtTime() + "_" + stmt.getAmount();
                }
            } else {
                key = stmt.getOutTradeNo() != null && !stmt.getOutTradeNo().isEmpty()
                        ? stmt.getOutTradeNo()
                        : stmt.getPlatformCode() + "_" + stmt.getStmtTime() + "_" + stmt.getAmount();
            }
            
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(stmt);
        }
        
        return groups;
    }
}
