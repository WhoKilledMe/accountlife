package com.acco.life.service;

import com.acco.life.dto.TransactionCategoryDto;
import reactor.core.publisher.Mono;
import com.acco.life.common.PageResponse;

import java.util.List;

/**
 * description: 交易分类服务接口，定义交易分类的增删改查操作
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
public interface TransactionCategoryService {

     Mono<List<TransactionCategoryDto>> findAll();

     Mono<TransactionCategoryDto> findById(Long id);

     Mono<TransactionCategoryDto> save(Mono<TransactionCategoryDto> entity);

     Mono<Void> deleteById(Long id);

      /**
       * 以父子树形结构返回所有系统与用户分类
       */
      Mono<List<TransactionCategoryDto>> findTree();

     /**
      * 物理分页+模糊搜索（名称），仅返回当前用户与系统分类，按 userId DESC, id DESC
      */
     Mono<PageResponse<TransactionCategoryDto>> page(String nameLike, int page, int size);
}