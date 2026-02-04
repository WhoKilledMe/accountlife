package com.acco.life.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * CSV工具类
 * 用于处理CSV文件解析的通用功能
 *
 * @author wensen.zhang
 */
public class CsvUtil {

    /**
     * 跳过CSV文件的前N行，返回从指定行开始的输入流（使用UTF-8编码）
     * 使用ByteArrayInputStream将剩余内容转换为新的输入流
     * 
     * @param inputStream 原始输入流
     * @param skipLines 需要跳过的行数
     * @return 跳过指定行数后的输入流
     * @throws IOException 如果读取失败
     */
    public static InputStream skipLines(InputStream inputStream, int skipLines) throws IOException {
        return skipLines(inputStream, skipLines, StandardCharsets.UTF_8);
    }

    /**
     * 跳过CSV文件的前N行，返回从指定行开始的输入流（支持自定义字符集）
     * 使用ByteArrayInputStream将剩余内容转换为新的输入流
     * 
     * @param inputStream 原始输入流
     * @param skipLines 需要跳过的行数
     * @param charset 字符集编码
     * @return 跳过指定行数后的输入流
     * @throws IOException 如果读取失败
     */
    public static InputStream skipLines(InputStream inputStream, int skipLines, Charset charset) throws IOException {
        if (skipLines <= 0) {
            return inputStream;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
        
        // 跳过指定行数
        for (int i = 0; i < skipLines; i++) {
            String line = reader.readLine();
            if (line == null) {
                break; // 文件已读完
            }
        }

        // 读取剩余内容
        StringBuilder remaining = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            remaining.append(line).append("\n");
        }
        reader.close();

        // 将剩余内容转换为新的输入流（使用UTF-8，因为后续Jackson解析器需要UTF-8）
        byte[] bytes = remaining.toString().getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(bytes);
    }

    /**
     * 从包含指定表头关键字的行开始读取，丢弃之前的所有元数据行（使用UTF-8编码）
     * 常用于类似京东、支付宝这类账单，前面有多行说明，真正表头行为"交易时间,商户名称,..."
     *
     * @param inputStream 原始输入流
     * @param headerPrefix 表头行的前缀（如 "交易时间"）
     * @return 从表头行开始的输入流（包含表头行本身）
     * @throws IOException 如果读取失败
     */
    public static InputStream skipUntilHeader(InputStream inputStream, String headerPrefix) throws IOException {
        return skipUntilHeader(inputStream, headerPrefix, StandardCharsets.UTF_8);
    }

    /**
     * 从包含指定表头关键字的行开始读取，丢弃之前的所有元数据行（支持自定义字符集）
     * 常用于类似京东、支付宝这类账单，前面有多行说明，真正表头行为"交易时间,商户名称,..."
     *
     * @param inputStream 原始输入流
     * @param headerPrefix 表头行的前缀（如 "交易时间"）
     * @param charset 字符集编码
     * @return 从表头行开始的输入流（包含表头行本身）
     * @throws IOException 如果读取失败
     */
    public static InputStream skipUntilHeader(InputStream inputStream, String headerPrefix, Charset charset) throws IOException {
        if (headerPrefix == null || headerPrefix.isEmpty()) {
            return inputStream;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));

        StringBuilder remaining = new StringBuilder();
        String line;
        boolean headerFound = false;

        while ((line = reader.readLine()) != null) {
            if (!headerFound) {
                // 找到表头行，从这一行开始保留
                if (line.startsWith(headerPrefix)) {
                    headerFound = true;
                    remaining.append(line).append("\n");
                }
            } else {
                remaining.append(line).append("\n");
            }
        }
        reader.close();

        // 将剩余内容转换为新的输入流（使用UTF-8，因为后续Jackson解析器需要UTF-8）
        byte[] bytes = remaining.toString().getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(bytes);
    }
}
