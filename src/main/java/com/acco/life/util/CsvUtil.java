package com.acco.life.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * CSV工具类
 * 用于处理CSV文件解析的通用功能
 *
 * @author wensen.zhang
 */
public class CsvUtil {

    /**
     * 跳过CSV文件的前N行，返回从指定行开始的输入流
     * 使用ByteArrayInputStream将剩余内容转换为新的输入流
     * 
     * @param inputStream 原始输入流
     * @param skipLines 需要跳过的行数
     * @return 跳过指定行数后的输入流
     * @throws IOException 如果读取失败
     */
    public static InputStream skipLines(InputStream inputStream, int skipLines) throws IOException {
        if (skipLines <= 0) {
            return inputStream;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        
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

        // 将剩余内容转换为新的输入流
        byte[] bytes = remaining.toString().getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(bytes);
    }
}
