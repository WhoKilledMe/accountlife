package com.acco.life.service.impl;

import com.acco.life.dto.TransactionCategoryDto;
import com.acco.life.mapper.TransactionCategoryMapper;
import com.acco.life.repository.TransactionCategoryRepository;
import com.acco.life.service.TransactionCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        return repository.findAll()
                .map(mapper::toDto).collectList();
    }

    @Override
    public Mono<TransactionCategoryDto> findById(Integer id) {
        return repository.findById(id)
        .map(mapper::toDto);
    }

    @Override
    public Mono<TransactionCategoryDto> save(Mono<TransactionCategoryDto> dto) {

        return dto.map(mapper::toEntity)
            .flatMap(repository::save)
            .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<List<TransactionCategoryDto>> findTree() {
        return repository.findAll()
                .map(mapper::toDto)
                .collectList()
                .map(this::buildTree);
    }

    private List<TransactionCategoryDto> buildTree(List<TransactionCategoryDto> flatList) {
        // 排序：type 升序 -> sortOrder 升序 -> id 升序，保证稳定
        flatList.sort(Comparator
                .comparing(TransactionCategoryDto::getType, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(TransactionCategoryDto::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(TransactionCategoryDto::getId, Comparator.nullsLast(Integer::compareTo)));

        Map<Integer, TransactionCategoryDto> idToNode = new HashMap<>();
        for (TransactionCategoryDto node : flatList) {
            idToNode.put(node.getId(), node);
            if (node.getChildren() == null) {
                node.setChildren(new ArrayList<>());
            }
        }

        List<TransactionCategoryDto> roots = new ArrayList<>();
        for (TransactionCategoryDto node : flatList) {
            Integer parentId = node.getParentId();
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
                .comparing(TransactionCategoryDto::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(TransactionCategoryDto::getId, Comparator.nullsLast(Integer::compareTo)));
        for (TransactionCategoryDto child : node.getChildren()) {
            sortRecursively(child);
        }
    }
}
