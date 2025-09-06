package com.acco.life.service.impl;

import com.acco.life.dto.TransactionCategoryDto;
import com.acco.life.mapper.TransactionCategoryMapper;
import com.acco.life.repository.TransactionCategoryRepository;
import com.acco.life.service.TransactionCategoryService;
import com.acco.life.exception.BusinessException;
import com.acco.life.entity.TransactionCategory;
import com.acco.life.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * description: 交易分类服务实现类，实现交易分类的增删改查业务逻辑
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */

@Service
@RequiredArgsConstructor
public class TransactionCategoryServiceImpl implements TransactionCategoryService {

    private final TransactionCategoryRepository repository;

    private final TransactionCategoryMapper mapper;


    @Override
    public Mono<List<TransactionCategoryDto>> findAll() {
        return com.acco.life.util.UserUtil.getCurrentUserId()
                .flatMapMany(uid -> repository.findAllForUserOrSystemOrdered(uid))
                .map(mapper::toDto)
                .collectList();
    }

    @Override
    public Mono<TransactionCategoryDto> findById(Long  id) {
        return repository.findById(id)
        .map(mapper::toDto);
    }

    @Override
    public Mono<TransactionCategoryDto> save(Mono<TransactionCategoryDto> dto) {
        return dto.flatMap(incomingDto -> {
            Long id = incomingDto.getId();
            if (id == null) {
                // 创建：直接保存为用户分类（允许），系统分类由运维/脚本维护
                TransactionCategory entity = mapper.toEntity(incomingDto);
                return repository.save(entity).map(mapper::toDto);
            }
            // 更新：禁止修改系统分类
            return repository.findById(id)
                    .switchIfEmpty(Mono.error(new BusinessException("分类不存在")))
                    .flatMap(existing -> {
                        if (existing.getUserId() == null) {
                            return Mono.error(new BusinessException("系统分类禁止修改"));
                        }
                        TransactionCategory toSave = mapper.toEntity(incomingDto);
                        return repository.save(toSave).map(mapper::toDto);
                    });
        });
    }

    @Override
    public Mono<Void> deleteById(Long  id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException("分类不存在")))
                .flatMap(existing -> {
                    if (existing.getUserId() == null) {
                        return Mono.error(new BusinessException("系统分类禁止删除"));
                    }
                    return repository.deleteById(id);
                });
    }

    @Override
    public Mono<List<TransactionCategoryDto>> findTree() {
        return com.acco.life.util.UserUtil.getCurrentUserId()
                .flatMapMany(uid -> repository.findAllForUserOrSystemOrdered(uid))
                .map(mapper::toDto)
                .collectList()
                .map(this::buildTree);
    }

    @Override
    public Mono<PageResponse<TransactionCategoryDto>> page(String nameLike, int page, int size) {
        final int currentPage = Math.max(page, 0);
        final int pageSize = Math.max(size, 1);
        long offset = (long) currentPage * pageSize;

        return com.acco.life.util.UserUtil.getCurrentUserId()
                .switchIfEmpty(Mono.just(-1L))
                .flatMap(uid -> {
                    String like = (nameLike == null || nameLike.isEmpty()) ? null : ("%" + nameLike + "%");
                    Mono<List<TransactionCategoryDto>> dataMono = repository
                            .searchForUserOrSystem(uid, like, pageSize, offset)
                            .map(mapper::toDto)
                            .collectList();
                    Mono<Long> countMono = repository.countSearchForUserOrSystem(uid, like);
                    return Mono.zip(dataMono, countMono)
                            .map(tuple -> PageResponse.of(tuple.getT1(), currentPage, pageSize, tuple.getT2()));
                });
    }

    private List<TransactionCategoryDto> buildTree(List<TransactionCategoryDto> flatList) {
        // 排序：type 升序 -> sortOrder 升序 -> id 升序，保证稳定
        flatList.sort(Comparator
                .comparing(TransactionCategoryDto::getType, Comparator.nullsLast(Integer ::compareTo))
                .thenComparing(TransactionCategoryDto::getSortOrder, Comparator.nullsLast(Integer ::compareTo))
                .thenComparing(TransactionCategoryDto::getId, Comparator.nullsLast(Long ::compareTo)));

        Map<Long , TransactionCategoryDto> idToNode = new HashMap<>();
        for (TransactionCategoryDto node : flatList) {
            idToNode.put(node.getId(), node);
            if (node.getChildren() == null) {
                node.setChildren(new ArrayList<>());
            }
        }

        List<TransactionCategoryDto> roots = new ArrayList<>();
        for (TransactionCategoryDto node : flatList) {
            Long  parentId = node.getParentId();
            if (parentId == null) {
                roots.add(node);
            } else {
                TransactionCategoryDto parent = idToNode.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(node);
                } else {
                    // 无父节点（数据异常）时，视为根
                    roots.add(node);
                }
            }
        }

        // 对每个父节点的子节点再按 sortOrder 排序
        for (TransactionCategoryDto root : roots) {
            sortRecursively(root);
        }

        return roots;
    }

    private void sortRecursively(TransactionCategoryDto node) {
        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            return;
        }
        node.getChildren().sort(Comparator
                .comparing(TransactionCategoryDto::getSortOrder, Comparator.nullsLast(Integer ::compareTo))
                .thenComparing(TransactionCategoryDto::getId, Comparator.nullsLast(Long ::compareTo)));
        for (TransactionCategoryDto child : node.getChildren()) {
            sortRecursively(child);
        }
    }
}
