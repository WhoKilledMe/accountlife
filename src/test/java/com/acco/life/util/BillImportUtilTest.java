package com.acco.life.util;

import com.acco.life.enums.TransactionSourceType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

/**
 * 账单导入工具测试类
 * 
 * 注意：这是一个集成测试，需要数据库连接
 * 运行前请确保：
 * 1. 数据库已配置
 * 2. 文件路径正确
 * 3. 用户ID有效
 */
@SpringBootTest
public class BillImportUtilTest {

    @Autowired
    private BillImportUtil billImportUtil;

    /**
     * 测试导入美团账单
     */
    @Test
    public void testImportMeituanBill() {
        String filePath = "src/main/resources/csv/美团账单(20250823-20250923).csv";
        Long userId = 1L; // 替换为实际的用户ID

        Mono<BillImportUtil.ImportResult> result = billImportUtil.importBillFile(
                filePath, TransactionSourceType.PLATFORM, userId);

        StepVerifier.create(result)
                .assertNext(importResult -> {
                    System.out.println("导入结果: " + importResult);
                    assert importResult.isSuccess() : "导入应该成功";
                    assert importResult.getTotalRows() > 0 : "应该有数据行";
                })
                .verifyComplete();
    }

    /**
     * 测试导入支付宝账单
     */
    @Test
    public void testImportAlipayBill() {
        String filePath = "src/main/resources/csv/支付宝交易明细(20240904-20250903)_副本.csv";
        Long userId = 1L; // 替换为实际的用户ID

        Mono<BillImportUtil.ImportResult> result = billImportUtil.importBillFile(
                filePath, TransactionSourceType.ALIPAY, userId);

        StepVerifier.create(result)
                .assertNext(importResult -> {
                    System.out.println("导入结果: " + importResult);
                    assert importResult.isSuccess() : "导入应该成功";
                    assert importResult.getTotalRows() > 0 : "应该有数据行";
                })
                .verifyComplete();
    }

    /**
     * 测试导入LabelDetail账单
     */
    @Test
    public void testImportLabelDetailBill() {
        String filePath = "src/main/resources/csv/labelDetail.csv";
        Long userId = 1L; // 替换为实际的用户ID

        Mono<BillImportUtil.ImportResult> result = billImportUtil.importBillFile(
                filePath, TransactionSourceType.LABEL_DETAIL, userId);

        StepVerifier.create(result)
                .assertNext(importResult -> {
                    System.out.println("导入结果: " + importResult);
                    assert importResult.isSuccess() : "导入应该成功";
                    assert importResult.getTotalRows() > 0 : "应该有数据行";
                })
                .verifyComplete();
    }

    /**
     * 测试批量导入
     */
    @Test
    public void testBatchImport() {
        List<String> filePaths = Arrays.asList(
                "src/main/resources/csv/美团账单(20250823-20250923).csv",
                "src/main/resources/csv/支付宝交易明细(20240904-20250903)_副本.csv",
                "src/main/resources/csv/labelDetail.csv"
        );
        Long userId = 1L; // 替换为实际的用户ID

        Mono<List<BillImportUtil.ImportResult>> results = billImportUtil.importBillFiles(
                filePaths, TransactionSourceType.PLATFORM, userId);

        StepVerifier.create(results)
                .assertNext(resultList -> {
                    System.out.println("批量导入结果数量: " + resultList.size());
                    for (BillImportUtil.ImportResult result : resultList) {
                        System.out.println("  - " + result);
                    }
                })
                .verifyComplete();
    }
}
