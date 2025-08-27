package com.acco.life.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.acco.life.dto.UserDto;
import com.acco.life.dto.UserGroupDto;
import com.acco.life.entity.UserGroup;
import com.acco.life.entity.UserGroupMember;
import com.acco.life.mapper.UserGroupMapper;
import com.acco.life.mapper.UserMapper;
import com.acco.life.repository.UserGroupMemberRepository;
import com.acco.life.repository.UserGroupRepository;
import com.acco.life.repository.UserRepository;
import com.acco.life.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import com.acco.life.util.PasswordUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * description: 用户服务实现类，实现用户相关业务逻辑
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final UserGroupRepository groupRepository;

    private final UserGroupMemberRepository memberRepository;

    private final UserMapper mapper;

    private final UserGroupMapper groupMapper;


    @Override
    public Mono<List<UserDto>> findAll() {
        return repository.findAll()
                .map(mapper::toDto).collectList();
    }

    @Override
    public Mono<UserDto> findById(Integer id) {
        return repository.findById(id)
                .map(mapper::toDto);
    }

    @Transactional
    @Override
    public Mono<UserDto> save(Mono<UserDto> dtoMono) {
        return dtoMono
                .flatMap(this::ensureGroupId)
                .flatMap(this::saveUserAccount)
                .flatMap(this::bindGroupMember);
    }

    private Mono<UserDto> ensureGroupId(UserDto dto) {
        if (Objects.nonNull(dto.getGroupId())) {
            return Mono.just(dto);
        }

        return createUserGroup(dto.getUsername())
                .map(group -> {
                    dto.setGroupId(group.getId());
                    return dto;
                });
    }

    private Mono<UserDto> saveUserAccount(UserDto dto) {
        return Mono.just(dto)
                .map(in -> {
                    if (in.getPassword() != null && !in.getPassword().isEmpty()) {
                        String salt = PasswordUtil.generateSalt(16);
                        String salted = PasswordUtil.md5WithSalt(in.getPassword(), salt);
                        in.setPassword(salted);
                    } else {
                        in.setPassword(null);
                    }
                    return in;
                })
                .map(mapper::toEntity)
                .flatMap(repository::save)
                .map(entity -> mapper.dtoToDto(dto, entity.getId(),dto.getGroupId(), 1));
    }

    private Mono<UserDto> bindGroupMember(UserDto dto) {
        Integer groupId = dto.getGroupId();
        if (ObjectUtil.isNull(groupId)) {
            return Mono.just(dto);
        }

        UserGroupMember member = new UserGroupMember();
        member.setUserId(dto.getId());
        member.setGroupId(groupId);
        member.setRole(dto.getRole());

        return memberRepository.save(member).thenReturn(dto);
    }

    private Mono<UserGroup> createUserGroup(String username) {
        UserGroupDto userGroupDto = new UserGroupDto();
        userGroupDto.setName(username + "家庭组");
        userGroupDto.setDescription(username + "家庭组");
        userGroupDto.setCreatedAt(LocalDateTime.now());

        return groupRepository.save(groupMapper.toEntity(userGroupDto));
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        return repository.deleteById(id);
    }

    public static void main(String[] args) {
        String salt = PasswordUtil.generateSalt(16);
        String salted = PasswordUtil.md5WithSalt("123456", salt);
        System.out.println(salted);
    }
}
