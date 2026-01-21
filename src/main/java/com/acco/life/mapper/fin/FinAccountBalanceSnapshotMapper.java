package com.acco.life.mapper.fin;

import com.acco.life.dto.fin.FinAccountBalanceSnapshotDto;
import com.acco.life.entity.fin.FinAccountBalanceSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 账户余额快照 Mapper
 *
 * @author wensen.zhang
 * @version V2.0.0
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinAccountBalanceSnapshotMapper {

    FinAccountBalanceSnapshotDto toDto(FinAccountBalanceSnapshot entity);

    FinAccountBalanceSnapshot toEntity(FinAccountBalanceSnapshotDto dto);

    void updateEntityFromDto(FinAccountBalanceSnapshotDto dto, @MappingTarget FinAccountBalanceSnapshot entity);
}
