package com.acco.life.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Collections;

/**
 * 分页响应类
 *
 * @author wensen.zhang
 * @version V1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    
    /**
     * 数据列表
     */
    private List<T> content;
    
    /**
     * 当前页码
     */
    private int pageNumber;
    
    /**
     * 每页大小
     */
    private int pageSize;
    
    /**
     * 总记录数
     */
    private long totalElements;
    
    /**
     * 总页数
     */
    private int totalPages;
    
    /**
     * 是否有下一页
     */
    private boolean hasNext;
    
    /**
     * 是否有上一页
     */
    private boolean hasPrevious;
    
    /**
     * 是否为第一页
     */
    private boolean isFirst;
    
    /**
     * 是否为最后一页
     */
    private boolean isLast;
    
    /**
     * 从Spring Data Page转换为PageResponse
     */
    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }

    /**
     * 从内存 List 构建分页响应
     */
    public static <T> PageResponse<T> fromList(List<T> allItems, int pageNumber, int pageSize) {
        if (pageSize <= 0) {
            pageSize = 10;
        }
        if (pageNumber < 0) {
            pageNumber = 0;
        }
        int totalElements = allItems == null ? 0 : allItems.size();
        int totalPages = pageSize == 0 ? 0 : (int) Math.ceil(totalElements / (double) pageSize);
        int fromIndex = Math.min(pageNumber * pageSize, totalElements);
        int toIndex = Math.min(fromIndex + pageSize, totalElements);
        List<T> content = (allItems == null || allItems.isEmpty()) ? Collections.emptyList() : allItems.subList(fromIndex, toIndex);

        return PageResponse.<T>builder()
                .content(content)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNext(pageNumber + 1 < totalPages)
                .hasPrevious(pageNumber > 0 && totalElements > 0)
                .isFirst(pageNumber == 0 || totalElements == 0)
                .isLast(totalPages == 0 || pageNumber + 1 == totalPages)
                .build();
    }

    /**
     * 直接构建分页响应（适用于数据库分页）
     */
    public static <T> PageResponse<T> of(List<T> content, int pageNumber, int pageSize, long totalElements) {
        if (pageSize <= 0) {
            pageSize = 10;
        }
        if (pageNumber < 0) {
            pageNumber = 0;
        }
        int totalPages = pageSize == 0 ? 0 : (int) Math.ceil(totalElements / (double) pageSize);
        return PageResponse.<T>builder()
                .content(content)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNext(pageNumber + 1 < totalPages)
                .hasPrevious(pageNumber > 0 && totalElements > 0)
                .isFirst(pageNumber == 0 || totalElements == 0)
                .isLast(totalPages == 0 || pageNumber + 1 == totalPages)
                .build();
    }
} 