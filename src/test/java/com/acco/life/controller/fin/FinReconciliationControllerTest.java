package com.acco.life.controller.fin;

import com.acco.life.dto.fin.FinStatementAccountMapDto;
import com.acco.life.dto.fin.FinTransactionDto;
import com.acco.life.dto.fin.FinTransactionStatementMapDto;
import com.acco.life.filter.UserLoginFilter;
import com.acco.life.service.AuthService;
import com.acco.life.service.fin.FinReconciliationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * 对账归并 Controller 单元测试
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@WebFluxTest(FinReconciliationController.class)
@Import(UserLoginFilter.class)
class FinReconciliationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FinReconciliationService reconciliationService;

    @MockBean
    private AuthService authService;

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_STATEMENT_ID = 100L;
    private static final Long TEST_ACCOUNT_ID = 200L;
    private static final Long TEST_TRANSACTION_ID = 300L;
    private static final String TEST_TOKEN = "testtoken";

    @BeforeEach
    void setUp() {
        // Mock AuthService 返回 userId
        when(authService.getUserIdByToken(TEST_TOKEN)).thenReturn(Mono.just(TEST_USER_ID));
    }

    /**
     * 测试自动对账：将账单映射到账户
     */
    @Test
    void testAutoMapStatementToAccount() {
        // 准备测试数据
        FinStatementAccountMapDto mapDto = createStatementAccountMapDto();
        List<FinStatementAccountMapDto> mapList = Collections.singletonList(mapDto);

        // Mock 服务方法
        when(reconciliationService.autoMapStatementToAccount(TEST_USER_ID))
                .thenReturn(Mono.just(mapList));

        // 执行测试
        webTestClient.post()
                .uri("/api/fin/reconciliation/auto-map-account")
                .header("Authorization", "Bearer " + TEST_TOKEN)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(FinStatementAccountMapDto.class)
                .hasSize(1)
                .consumeWith(result -> {
                    List<FinStatementAccountMapDto> body = result.getResponseBody();
                    assert body != null && !body.isEmpty();
                    FinStatementAccountMapDto dto = body.get(0);
                    assert dto != null;
                    assert dto.getStatementId().equals(TEST_STATEMENT_ID);
                    assert dto.getAccountId().equals(TEST_ACCOUNT_ID);
                });
    }

    /**
     * 测试手动映射账单到账户
     */
    @Test
    void testManualMapStatementToAccount() {
        // 准备测试数据
        FinStatementAccountMapDto mapDto = createStatementAccountMapDto();
        String remark = "测试备注";

        // Mock 服务方法
        when(reconciliationService.manualMapStatementToAccount(TEST_STATEMENT_ID, TEST_ACCOUNT_ID, remark))
                .thenReturn(Mono.just(mapDto));

        // 执行测试
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/fin/reconciliation/map-account")
                        .queryParam("statementId", TEST_STATEMENT_ID)
                        .queryParam("accountId", TEST_ACCOUNT_ID)
                        .queryParam("remark", remark)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(FinStatementAccountMapDto.class)
                .consumeWith(result -> {
                    FinStatementAccountMapDto dto = result.getResponseBody();
                    assert dto != null;
                    assert dto.getStatementId().equals(TEST_STATEMENT_ID);
                    assert dto.getAccountId().equals(TEST_ACCOUNT_ID);
                });
    }

    /**
     * 测试手动映射账单到账户（无备注）
     */
    @Test
    void testManualMapStatementToAccountWithoutRemark() {
        // 准备测试数据
        FinStatementAccountMapDto mapDto = createStatementAccountMapDto();

        // Mock 服务方法
        when(reconciliationService.manualMapStatementToAccount(TEST_STATEMENT_ID, TEST_ACCOUNT_ID, null))
                .thenReturn(Mono.just(mapDto));

        // 执行测试
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/fin/reconciliation/map-account")
                        .queryParam("statementId", TEST_STATEMENT_ID)
                        .queryParam("accountId", TEST_ACCOUNT_ID)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(FinStatementAccountMapDto.class);
    }

    /**
     * 测试自动归并：将账单归并到交易
     */
    @Test
    void testAutoMergeStatementToTransaction() {
        // 准备测试数据
        FinTransactionStatementMapDto mapDto = createTransactionStatementMapDto();
        List<FinTransactionStatementMapDto> mapList = Collections.singletonList(mapDto);

        // Mock 服务方法
        when(reconciliationService.autoMergeStatementToTransaction(TEST_USER_ID))
                .thenReturn(Mono.just(mapList));

        // 执行测试
        webTestClient.post()
                .uri("/api/fin/reconciliation/auto-merge-transaction")
                .header("Authorization", "Bearer " + TEST_TOKEN)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(FinTransactionStatementMapDto.class)
                .hasSize(1)
                .consumeWith(result -> {
                    List<FinTransactionStatementMapDto> body = result.getResponseBody();
                    assert body != null && !body.isEmpty();
                    FinTransactionStatementMapDto dto = body.get(0);
                    assert dto != null;
                    assert dto.getStatementId().equals(TEST_STATEMENT_ID);
                    assert dto.getTransactionId().equals(TEST_TRANSACTION_ID);
                });
    }

    /**
     * 测试手动归并账单到交易
     */
    @Test
    void testManualMergeStatementToTransaction() {
        // 准备测试数据
        FinTransactionStatementMapDto mapDto = createTransactionStatementMapDto();
        String mapType = "ONE_TO_ONE";

        // Mock 服务方法
        when(reconciliationService.manualMergeStatementToTransaction(TEST_STATEMENT_ID, TEST_TRANSACTION_ID, mapType))
                .thenReturn(Mono.just(mapDto));

        // 执行测试
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/fin/reconciliation/merge-transaction")
                        .queryParam("statementId", TEST_STATEMENT_ID)
                        .queryParam("transactionId", TEST_TRANSACTION_ID)
                        .queryParam("mapType", mapType)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(FinTransactionStatementMapDto.class)
                .consumeWith(result -> {
                    FinTransactionStatementMapDto dto = result.getResponseBody();
                    assert dto != null;
                    assert dto.getStatementId().equals(TEST_STATEMENT_ID);
                    assert dto.getTransactionId().equals(TEST_TRANSACTION_ID);
                });
    }

    /**
     * 测试手动归并账单到交易（无 mapType）
     */
    @Test
    void testManualMergeStatementToTransactionWithoutMapType() {
        // 准备测试数据
        FinTransactionStatementMapDto mapDto = createTransactionStatementMapDto();

        // Mock 服务方法
        when(reconciliationService.manualMergeStatementToTransaction(TEST_STATEMENT_ID, TEST_TRANSACTION_ID, null))
                .thenReturn(Mono.just(mapDto));

        // 执行测试
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/fin/reconciliation/merge-transaction")
                        .queryParam("statementId", TEST_STATEMENT_ID)
                        .queryParam("transactionId", TEST_TRANSACTION_ID)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(FinTransactionStatementMapDto.class);
    }

    /**
     * 测试从账单创建交易
     */
    @Test
    void testCreateTransactionFromStatement() {
        // 准备测试数据
        FinTransactionDto transactionDto = createTransactionDto();

        // Mock 服务方法
        when(reconciliationService.createTransactionFromStatement(TEST_STATEMENT_ID))
                .thenReturn(Mono.just(transactionDto));

        // 执行测试
        webTestClient.post()
                .uri("/api/fin/reconciliation/create-transaction/{statementId}", TEST_STATEMENT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(FinTransactionDto.class)
                .consumeWith(result -> {
                    FinTransactionDto dto = result.getResponseBody();
                    assert dto != null;
                    assert dto.getUserId().equals(TEST_USER_ID);
                    assert dto.getAmount() != null;
                });
    }

    /**
     * 测试从多条账单创建交易（合单）
     */
    @Test
    void testCreateTransactionFromStatements() {
        // 准备测试数据
        List<Long> statementIds = Arrays.asList(TEST_STATEMENT_ID, TEST_STATEMENT_ID + 1);
        FinTransactionDto transactionDto = createTransactionDto();
        String bizType = "PAY";

        // Mock 服务方法
        when(reconciliationService.createTransactionFromStatements(statementIds, bizType))
                .thenReturn(Mono.just(transactionDto));

        // 执行测试
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/fin/reconciliation/create-transaction-batch")
                        .queryParam("bizType", bizType)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(statementIds)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(FinTransactionDto.class)
                .consumeWith(result -> {
                    FinTransactionDto dto = result.getResponseBody();
                    assert dto != null;
                    assert dto.getUserId().equals(TEST_USER_ID);
                });
    }

    /**
     * 测试从多条账单创建交易（无 bizType）
     */
    @Test
    void testCreateTransactionFromStatementsWithoutBizType() {
        // 准备测试数据
        List<Long> statementIds = Arrays.asList(TEST_STATEMENT_ID, TEST_STATEMENT_ID + 1);
        FinTransactionDto transactionDto = createTransactionDto();

        // Mock 服务方法
        when(reconciliationService.createTransactionFromStatements(statementIds, null))
                .thenReturn(Mono.just(transactionDto));

        // 执行测试
        webTestClient.post()
                .uri("/api/fin/reconciliation/create-transaction-batch")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(statementIds)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FinTransactionDto.class);
    }

    /**
     * 测试查询账单的账户映射
     */
    @Test
    void testFindAccountMapsByStatementId() {
        // 准备测试数据
        FinStatementAccountMapDto mapDto = createStatementAccountMapDto();
        List<FinStatementAccountMapDto> mapList = Collections.singletonList(mapDto);

        // Mock 服务方法
        when(reconciliationService.findAccountMapsByStatementId(TEST_STATEMENT_ID))
                .thenReturn(Mono.just(mapList));

        // 执行测试
        webTestClient.get()
                .uri("/api/fin/reconciliation/statement/{statementId}/account-maps", TEST_STATEMENT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(FinStatementAccountMapDto.class)
                .hasSize(1);
    }

    /**
     * 测试查询账单的交易映射
     */
    @Test
    void testFindTransactionMapsByStatementId() {
        // 准备测试数据
        FinTransactionStatementMapDto mapDto = createTransactionStatementMapDto();
        List<FinTransactionStatementMapDto> mapList = Collections.singletonList(mapDto);

        // Mock 服务方法
        when(reconciliationService.findTransactionMapsByStatementId(TEST_STATEMENT_ID))
                .thenReturn(Mono.just(mapList));

        // 执行测试
        webTestClient.get()
                .uri("/api/fin/reconciliation/statement/{statementId}/transaction-maps", TEST_STATEMENT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(FinTransactionStatementMapDto.class)
                .hasSize(1);
    }

    /**
     * 测试查询交易的账单映射
     */
    @Test
    void testFindStatementMapsByTransactionId() {
        // 准备测试数据
        FinTransactionStatementMapDto mapDto = createTransactionStatementMapDto();
        List<FinTransactionStatementMapDto> mapList = Collections.singletonList(mapDto);

        // Mock 服务方法
        when(reconciliationService.findStatementMapsByTransactionId(TEST_TRANSACTION_ID))
                .thenReturn(Mono.just(mapList));

        // 执行测试
        webTestClient.get()
                .uri("/api/fin/reconciliation/transaction/{transactionId}/statement-maps", TEST_TRANSACTION_ID)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(FinTransactionStatementMapDto.class)
                .hasSize(1);
    }

    /**
     * 测试取消账单-账户映射
     */
    @Test
    void testCancelStatementAccountMap() {
        // Mock 服务方法
        when(reconciliationService.cancelStatementAccountMap(TEST_STATEMENT_ID, TEST_ACCOUNT_ID))
                .thenReturn(Mono.empty());

        // 执行测试
        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/fin/reconciliation/map-account")
                        .queryParam("statementId", TEST_STATEMENT_ID)
                        .queryParam("accountId", TEST_ACCOUNT_ID)
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    /**
     * 测试取消账单-交易映射
     */
    @Test
    void testCancelStatementTransactionMap() {
        // Mock 服务方法
        when(reconciliationService.cancelStatementTransactionMap(TEST_STATEMENT_ID, TEST_TRANSACTION_ID))
                .thenReturn(Mono.empty());

        // 执行测试
        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/fin/reconciliation/merge-transaction")
                        .queryParam("statementId", TEST_STATEMENT_ID)
                        .queryParam("transactionId", TEST_TRANSACTION_ID)
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    /**
     * 测试获取对账摘要
     */
    @Test
    void testGetReconciliationSummary() {
        // 准备测试数据
        FinReconciliationService.ReconciliationSummary summary =
                new FinReconciliationService.ReconciliationSummary(10, 5, 3, 2);

        // Mock 服务方法
        when(reconciliationService.getReconciliationSummary(TEST_USER_ID))
                .thenReturn(Mono.just(summary));

        // 执行测试
        webTestClient.get()
                .uri("/api/fin/reconciliation/summary")
                .header("Authorization", "Bearer " + TEST_TOKEN)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(FinReconciliationService.ReconciliationSummary.class)
                .consumeWith(result -> {
                    FinReconciliationService.ReconciliationSummary response = result.getResponseBody();
                    assert response != null;
                    assert response.totalPending() == 10;
                    assert response.mappedToAccount() == 5;
                    assert response.mappedToTransaction() == 3;
                    assert response.ignored() == 2;
                });
    }

    /**
     * 创建测试用的账单-账户映射 DTO
     */
    private FinStatementAccountMapDto createStatementAccountMapDto() {
        FinStatementAccountMapDto dto = new FinStatementAccountMapDto();
        dto.setId(1L);
        dto.setStatementId(TEST_STATEMENT_ID);
        dto.setAccountId(TEST_ACCOUNT_ID);
        dto.setMapType("MANUAL");
        dto.setConfidence(new BigDecimal("100.00"));
        dto.setMappedBy("USER");
        dto.setMappedAt(LocalDateTime.now());
        dto.setRemark("测试备注");
        dto.setAccountName("测试账户");
        dto.setMapTypeName("手动映射");
        return dto;
    }

    /**
     * 创建测试用的交易-账单映射 DTO
     */
    private FinTransactionStatementMapDto createTransactionStatementMapDto() {
        FinTransactionStatementMapDto dto = new FinTransactionStatementMapDto();
        dto.setId(1L);
        dto.setTransactionId(TEST_TRANSACTION_ID);
        dto.setStatementId(TEST_STATEMENT_ID);
        dto.setMapType("ONE_TO_ONE");
        dto.setAllocatedAmount(new BigDecimal("100.00"));
        dto.setConfirmStatus("MANUAL_CONFIRMED");
        dto.setConfidence(new BigDecimal("100.00"));
        dto.setMappedAt(LocalDateTime.now());
        dto.setMapTypeName("一对一");
        dto.setConfirmStatusName("手动确认");
        return dto;
    }

    /**
     * 创建测试用的交易 DTO
     */
    private FinTransactionDto createTransactionDto() {
        FinTransactionDto dto = new FinTransactionDto();
        dto.setId(1L);
        dto.setUserId(TEST_USER_ID);
        dto.setBizType("PAY");
        dto.setTradeTime(LocalDateTime.now());
        dto.setAmount(new BigDecimal("100.00"));
        dto.setCurrency("CNY");
        dto.setStatus("MATCHED");
        dto.setCounterparty("测试商户");
        dto.setPlatformCode("ALIPAY");
        dto.setRemark("测试交易");
        dto.setCategoryId(1L);
        return dto;
    }
}
