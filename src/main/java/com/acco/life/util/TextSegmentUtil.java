package com.acco.life.util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文本分词工具类
 * 用于将文本分词，便于关键词匹配
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
public class TextSegmentUtil {

    /**
     * 常见分隔符
     */
    private static final String DELIMITERS = "\\s+|[，。、；：！？,.;:!?\\-\\s]+";

    /**
     * 对文本进行分词
     * 策略：
     * 1. 先按分隔符分割
     * 2. 对每个片段进行滑动窗口分词（2-4字词）
     * 3. 保留原始文本和完整片段
     *
     * @param text 输入文本
     * @return 分词结果集合（去重，按长度降序排列，优先匹配长词）
     */
    public static Set<String> segment(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> segments = new LinkedHashSet<>();
        String normalized = text.trim();

        // 1. 添加完整文本（最高优先级）
        segments.add(normalized);

        // 2. 按分隔符分割
        String[] parts = normalized.split(DELIMITERS);
        for (String part : parts) {
            if (part.length() > 1) {
                segments.add(part);
            }
        }

        // 3. 滑动窗口分词（2-4字词）
        for (String part : parts) {
            if (part.length() >= 2) {
                // 2字词
                for (int i = 0; i <= part.length() - 2; i++) {
                    String word = part.substring(i, i + 2);
                    if (isValidWord(word)) {
                        segments.add(word);
                    }
                }
                // 3字词
                if (part.length() >= 3) {
                    for (int i = 0; i <= part.length() - 3; i++) {
                        String word = part.substring(i, i + 3);
                        if (isValidWord(word)) {
                            segments.add(word);
                        }
                    }
                }
                // 4字词
                if (part.length() >= 4) {
                    for (int i = 0; i <= part.length() - 4; i++) {
                        String word = part.substring(i, i + 4);
                        if (isValidWord(word)) {
                            segments.add(word);
                        }
                    }
                }
            }
        }

        // 4. 按长度降序排列（长词优先匹配）
        return segments.stream()
                .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * 判断是否为有效词（过滤纯数字、纯符号等）
     */
    private static boolean isValidWord(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        // 过滤纯数字
        if (word.matches("\\d+")) {
            return false;
        }
        // 过滤纯符号
        if (word.matches("[\\p{Punct}\\s]+")) {
            return false;
        }
        return true;
    }

    /**
     * 获取文本的所有可能分词（用于批量查询）
     * 返回按优先级排序的分词列表
     *
     * @param text 输入文本
     * @return 分词列表（按长度降序，长词优先）
     */
    public static List<String> getSegments(String text) {
        return new ArrayList<>(segment(text));
    }
}
