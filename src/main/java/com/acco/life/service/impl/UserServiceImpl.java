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
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author wensenzhang
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
                .map(mapper::toEntity)
                .flatMap(repository::save)
                .map(entity -> mapper.toDto(entity, dto.getGroupId()));
    }

    private Mono<UserDto> bindGroupMember(UserDto dto) {
        Integer groupId = dto.getGroupId();
        if (ObjectUtil.isNull(groupId)) {
            return Mono.just(dto);
        }

        UserGroupMember member = new UserGroupMember();
        member.setUserId(dto.getId());
        member.setGroupId(groupId);
        member.setRole(1);

        return memberRepository.save(member).thenReturn(dto);
    }

    private Mono<UserGroup> createUserGroup(String username) {
        UserGroupDto userGroupDto = new UserGroupDto();
        userGroupDto.setName(username + "默认用户组");
        userGroupDto.setDescription(username + "默认用户组");
        userGroupDto.setCreatedAt(LocalDateTime.now());

        return groupRepository.save(groupMapper.toEntity(userGroupDto));
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        return repository.deleteById(id);
    }
}
