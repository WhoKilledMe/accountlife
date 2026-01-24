package com.acco.life.service.csv;

import com.acco.life.dto.FileTransactionDto;
import com.acco.life.entity.fin.FinStatement;
import com.acco.life.enums.TransactionSourceType;
import com.acco.life.service.AiTransactionCategoryService;

import java.io.InputStream;
import java.util.List;

/**
 * 新版 CSV 解析策略接口
 * 解析 CSV 并返回 FinStatement 实体
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public interface FinStatementCsvParser {

    /**
     * 是否支持该来源类型
     */
    boolean supports(TransactionSourceType sourceType);

    /**
     * 获取平台代码
     */
    String getPlatformCode();

    /**
     * 获取解析器版本
     */
    String getParserVersion();

    /**
     * 将 CSV 输入流解析为原始 DTO 列表
     */
    List<? extends FileTransactionDto> parse(InputStream inputStream) throws Exception;

    /**
     * 将解析得到的 DTO 列表转换为 FinStatement 实体列表
     * 
     * @param dtos 解析后的 DTO 列表
     * @param userId 用户ID
     * @param fileId 文件ID
     * @param categoryService AI分类服务（可选，如果为null则不进行分类）
     * @return FinStatement 实体列表
     */
    List<FinStatement> buildStatements(List<? extends FileTransactionDto> dtos, Long userId, Long fileId, 
                                      AiTransactionCategoryService categoryService);
}
